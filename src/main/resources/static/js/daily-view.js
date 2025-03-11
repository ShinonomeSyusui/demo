document.addEventListener('DOMContentLoaded', function () {
    initializeTimeline();
    loadDailyTasks();
});

function initializeTimeline() {
    const timelineContent = document.getElementById('timeline-content');

    // 30分単位でタイムスロットを生成（6:00～23:30）
    for (let hour = 6; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const timeSlot = createTimeSlot(hour, minute);
            timelineContent.appendChild(timeSlot);
        }
    }
}

function createTimeSlot(hour, minute) {
    const timeSlot = document.createElement('div');
    timeSlot.className = 'time-slot';
    timeSlot.innerHTML = `
        <div class="time-column">${formatTime(hour, minute)}</div>
        <div class="task-column" ondrop="handleDrop(event)" ondragover="handleDragOver(event)"></div>
        <div class="status-column"></div>
        <div class="priority-column"></div>
    `;
    return timeSlot;
}

function formatTime(hour, minute) {
    return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
}

async function loadDailyTasks() {
    const selectedDate = document.getElementById('dateSelector').value;
    try {
        const response = await fetch(`/api/v1/tasks/daily?date=${selectedDate}`);
        const tasks = await response.json();
        renderTasks(tasks);
    } catch (error) {
        console.error('タスクの読み込みに失敗しました:', error);
    }
}

function renderTasks(tasks) {
    clearExistingTasks();
    tasks.forEach(task => {
        const timeSlot = findTimeSlot(task.startTime);
        if (timeSlot) {
            const taskElement = createTaskElement(task);
            timeSlot.querySelector('.task-column').appendChild(taskElement);
        }
    });
}

function createTaskElement(task) {
    const taskDiv = document.createElement('div');
    taskDiv.className = `task-item priority-${task.priority}`;
    taskDiv.draggable = true;
    taskDiv.setAttribute('data-task-id', task.id);
    taskDiv.innerHTML = `
        <div class="task-content">
            <span class="task-time">${task.getFormattedTimeSlot()}</span>
            <span class="task-title">${task.title}</span>
        </div>
        <div class="task-controls">
            <select class="status-select" onchange="updateTaskStatus(${task.id}, this.value)">
                <option value="未着手" ${task.status === '未着手' ? 'selected' : ''}>未着手</option>
                <option value="進行中" ${task.status === '進行中' ? 'selected' : ''}>進行中</option>
                <option value="完了" ${task.status === '完了' ? 'selected' : ''}>完了</option>
            </select>
        </div>
    `;

    taskDiv.addEventListener('dragstart', handleDragStart);
    taskDiv.addEventListener('click', () => showTaskDetail(task));

    return taskDiv;
}

function findTimeSlot(time) {
    const timeSlots = document.querySelectorAll('.time-slot');
    return Array.from(timeSlots).find(slot => {
        const slotTime = slot.querySelector('.time-column').textContent;
        return slotTime === time.substring(0, 5);
    });
}

function showTaskDetail(task) {
    const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
    document.getElementById('modalTaskTitle').textContent = task.title;
    document.getElementById('modalTaskTime').textContent = task.getFormattedTimeSlot();
    document.getElementById('modalTaskDescription').textContent = task.description;
    document.getElementById('modalTaskPriority').textContent = getPriorityText(task.priority);
    modal.show();
}

async function saveTaskChanges() {
    const taskId = currentTaskId;
    const startTime = document.getElementById('modalStartTime').value;
    const endTime = document.getElementById('modalEndTime').value;
    const priority = document.getElementById('modalTaskPriority').value;
    const status = document.getElementById('modalTaskStatus').value;

    try {
        // 時間の更新
        await updateTaskTime(taskId, startTime, endTime);

        // 優先度の更新
        await updateTaskPriority(taskId, priority);

        // ステータスの更新
        await updateTaskStatus(taskId, status);

        // モーダルを閉じる
        const modal = bootstrap.Modal.getInstance(document.getElementById('taskDetailModal'));
        modal.hide();

        // タスク一覧を再読み込み
        loadDailyTasks();

        showSuccessMessage('タスクを更新しました');
    } catch (error) {
        showErrorMessage('タスクの更新に失敗しました');
    }
}

function showSuccessMessage(message) {
    const toast = createToast(message, 'success');
    toast.show();
}

function showErrorMessage(message) {
    const toast = createToast(message, 'danger');
    toast.show();
}

function createToast(message, type) {
    const toastContainer = document.getElementById('toastContainer');
    const toastElement = document.createElement('div');
    toastElement.className = `toast align-items-center text-white bg-${type} border-0`;
    toastElement.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    toastContainer.appendChild(toastElement);
    return new bootstrap.Toast(toastElement);
}

// 日付操作関連の機能
function navigateDate(offset) {
    const dateSelector = document.getElementById('dateSelector');
    const currentDate = new Date(dateSelector.value);
    currentDate.setDate(currentDate.getDate() + offset);

    dateSelector.value = formatDate(currentDate);
    updateCurrentDateDisplay();
    loadDailyTasks();
}

function formatDate(date) {
    return date.toISOString().split('T')[0];
}

function updateCurrentDateDisplay() {
    const dateSelector = document.getElementById('dateSelector');
    const date = new Date(dateSelector.value);
    const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
    };

    document.getElementById('currentDate').textContent =
        date.toLocaleDateString('ja-JP', options);
}

// イベントリスナーの追加
document.getElementById('dateSelector').addEventListener('change', function () {
    updateCurrentDateDisplay();
    loadDailyTasks();
});

// 初期表示時の処理
document.addEventListener('DOMContentLoaded', function () {
    const today = new Date();
    document.getElementById('dateSelector').value = formatDate(today);
    updateCurrentDateDisplay();
    initializeTimeline();
    loadDailyTasks();
});

// 現在時刻インジケーターの更新
function updateCurrentTimeIndicator() {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();

    // 表示時間内かチェック（6:00-23:30）
    if (currentHour >= 6 && currentHour < 24) {
        const timeSlots = document.querySelectorAll('.time-slot');
        const indicator = document.createElement('div');
        indicator.className = 'current-time-indicator';

        // 既存のインジケーターを削除
        document.querySelectorAll('.current-time-indicator').forEach(el => el.remove());

        // 適切なタイムスロットを見つけてインジケーターを配置
        timeSlots.forEach(slot => {
            const slotTime = slot.querySelector('.time-column').textContent;
            const [slotHour, slotMinute] = slotTime.split(':').map(Number);

            if (currentHour === slotHour) {
                const position = (currentMinute / 60) * 100;
                indicator.style.top = `${position}%`;
                slot.querySelector('.task-column').appendChild(indicator.cloneNode(true));
            }
        });
    }
}

// 1分ごとに更新
setInterval(updateCurrentTimeIndicator, 60000);

// 初期表示時にも実行
document.addEventListener('DOMContentLoaded', function () {
    updateCurrentTimeIndicator();
});

// ドラッグ開始時の処理
function handleDragStart(event) {
    event.dataTransfer.setData('text/plain', event.target.getAttribute('data-task-id'));
    event.target.classList.add('dragging');
}

// ドラッグ中の処理
function handleDragOver(event) {
    event.preventDefault();
    const timeSlot = event.target.closest('.time-slot');
    if (timeSlot) {
        timeSlot.classList.add('drag-over');
    }
}

// ドロップ時の処理
async function handleDrop(event) {
    event.preventDefault();
    const taskId = event.dataTransfer.getData('text/plain');
    const timeSlot = event.target.closest('.time-slot');

    if (timeSlot) {
        const newTime = timeSlot.querySelector('.time-column').textContent;
        try {
            await updateTaskTime(taskId, newTime);
            loadDailyTasks(); // タスク一覧を再読み込み
            showSuccessMessage('タスク時間を更新しました');
        } catch (error) {
            showErrorMessage('タスク時間の更新に失敗しました');
        }
    }

    // ドラッグ効果をクリア
    document.querySelectorAll('.drag-over').forEach(el => el.classList.remove('drag-over'));
    document.querySelectorAll('.dragging').forEach(el => el.classList.remove('dragging'));
}

// タスクの時間調整機能
function initializeTimeAdjuster(taskId) {
    const startTime = document.getElementById('modalStartTime');
    const endTime = document.getElementById('modalEndTime');

    // 時間変更時の処理
    function handleTimeChange() {
        const start = new Date(`2000-01-01T${startTime.value}`);
        const end = new Date(`2000-01-01T${endTime.value}`);

        // 開始時間が終了時間より後の場合、終了時間を自動調整
        if (start > end) {
            end.setTime(start.getTime() + 30 * 60000); // 30分後
            endTime.value = end.toTimeString().slice(0, 5);
        }
    }

    startTime.addEventListener('change', handleTimeChange);
    endTime.addEventListener('change', handleTimeChange);
}

// 時間の更新をサーバーに送信
async function updateTaskTime(taskId, startTime, endTime) {
    try {
        const response = await fetch(`/api/v1/tasks/daily/${taskId}/time`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                startTime: startTime,
                endTime: endTime
            })
        });

        if (!response.ok) {
            throw new Error('時間の更新に失敗しました');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

// クイック編集機能
function initializeQuickEdit() {
    const timeSlots = document.querySelectorAll('.time-slot');

    timeSlots.forEach(slot => {
        slot.addEventListener('dblclick', (event) => {
            if (!event.target.closest('.task-item')) {
                const time = slot.querySelector('.time-column').textContent;
                showQuickAddTask(time);
            }
        });
    });
}

function showQuickAddTask(time) {
    const quickAddForm = document.createElement('div');
    quickAddForm.className = 'quick-add-form';
    quickAddForm.innerHTML = `
        <input type="text" class="form-control form-control-sm" placeholder="タスクを追加">
        <div class="quick-add-controls mt-1">
            <select class="form-select form-select-sm">
                <option value="1">至急！</option>
                <option value="2">高</option>
                <option value="3">中</option>
                <option value="4">低</option>
                <option value="5" selected>未設定</option>
            </select>
            <button class="btn btn-sm btn-primary" onclick="submitQuickAdd(this, '${time}')">追加</button>
        </div>
    `;

    const timeSlot = document.querySelector(`.time-slot:has(.time-column:contains("${time}"))`);
    timeSlot.querySelector('.task-column').appendChild(quickAddForm);
}

async function submitQuickAdd(button, time) {
    const form = button.closest('.quick-add-form');
    const title = form.querySelector('input').value;
    const priority = form.querySelector('select').value;

    if (!title.trim()) {
        showErrorMessage('タイトルを入力してください');
        return;
    }

    const taskData = {
        title: title,
        startTime: time,
        endTime: calculateEndTime(time, 30), // デフォルトで30分
        priority: priority,
        status: '未着手'
    };

    try {
        const response = await fetch('/api/v1/tasks/daily', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        });

        if (response.ok) {
            showSuccessMessage('タスクを追加しました');
            loadDailyTasks();
        } else {
            throw new Error('タスクの追加に失敗しました');
        }
    } catch (error) {
        showErrorMessage(error.message);
    }

    form.remove();
}

function calculateEndTime(startTime, durationMinutes) {
    const [hours, minutes] = startTime.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes + durationMinutes);
    return date.toTimeString().slice(0, 5);
}

function refreshDailyView() {
    // タスク一覧を再読み込み
    loadDailyTasks().then(() => {
        // タイムラインの更新
        updateTimelineDisplay();
        // 現在時刻インジケーターの更新
        updateCurrentTimeIndicator();
    });
}

function updateTimelineDisplay() {
    const timeSlots = document.querySelectorAll('.time-slot');
    timeSlots.forEach(slot => {
        const taskColumn = slot.querySelector('.task-column');
        const tasks = taskColumn.querySelectorAll('.task-item');

        // タスクの重なりを調整
        if (tasks.length > 1) {
            adjustTaskOverlap(tasks);
        }
    });
}

function adjustTaskOverlap(tasks) {
    const taskWidth = 100 / tasks.length;
    tasks.forEach((task, index) => {
        task.style.width = `${taskWidth}%`;
        task.style.left = `${taskWidth * index}%`;
        task.style.position = 'absolute';
    });
}

function checkTimeOverlap(startTime, endTime) {
    const existingTasks = document.querySelectorAll('.task-item');
    let hasOverlap = false;

    existingTasks.forEach(task => {
        const taskStart = task.getAttribute('data-start-time');
        const taskEnd = task.getAttribute('data-end-time');

        if (isTimeOverlapping(startTime, endTime, taskStart, taskEnd)) {
            hasOverlap = true;
            task.classList.add('overlap-warning');
        }
    });

    return hasOverlap;
}

function isTimeOverlapping(start1, end1, start2, end2) {
    return (start1 < end2 && end1 > start2);
}

function showOverlapWarning() {
    const warningElement = document.createElement('div');
    warningElement.className = 'overlap-warning-message';
    warningElement.textContent = '他のタスクと時間が重複しています';

    return new Promise((resolve) => {
        const confirmButton = document.createElement('button');
        confirmButton.textContent = '続行';
        confirmButton.onclick = () => resolve(true);

        const cancelButton = document.createElement('button');
        cancelButton.textContent = 'キャンセル';
        cancelButton.onclick = () => resolve(false);

        warningElement.appendChild(confirmButton);
        warningElement.appendChild(cancelButton);

        document.body.appendChild(warningElement);
    });
}

// タスクの時間調整機能の強化
function enhanceTimeAdjuster() {
    const timeControls = document.createElement('div');
    timeControls.className = 'time-adjust-controls';
    timeControls.innerHTML = `
        <div class="duration-control">
            <label>期間:</label>
            <select class="duration-select" onchange="adjustTaskDuration(this.value)">
                <option value="15">15分</option>
                <option value="30" selected>30分</option>
                <option value="45">45分</option>
                <option value="60">1時間</option>
                <option value="90">1時間30分</option>
                <option value="120">2時間</option>
                <option value="custom">カスタム</option>
            </select>
        </div>
        <div class="quick-time-buttons">
            <button onclick="quickAdjustTime(-15)">-15分</button>
            <button onclick="quickAdjustTime(15)">+15分</button>
        </div>
    `;

    return timeControls;
}

function adjustTaskDuration(minutes) {
    if (minutes === 'custom') {
        showCustomDurationInput();
        return;
    }

    const startTime = document.getElementById('modalStartTime').value;
    const endTime = calculateEndTime(startTime, parseInt(minutes));
    document.getElementById('modalEndTime').value = endTime;
}

function quickAdjustTime(minutesDiff) {
    const startInput = document.getElementById('modalStartTime');
    const endInput = document.getElementById('modalEndTime');

    let startTime = new Date(`2000-01-01T${startInput.value}`);
    let endTime = new Date(`2000-01-01T${endInput.value}`);

    startTime.setMinutes(startTime.getMinutes() + minutesDiff);
    endTime.setMinutes(endTime.getMinutes() + minutesDiff);

    startInput.value = startTime.toTimeString().slice(0, 5);
    endInput.value = endTime.toTimeString().slice(0, 5);
}

function showCustomDurationInput() {
    const customTimeInput = document.createElement('div');
    customTimeInput.className = 'custom-time-input';
    customTimeInput.innerHTML = `
        <div class="time-input-group">
            <input type="number" class="form-control" id="customHours" min="0" max="12" placeholder="時間">
            <span>時間</span>
            <input type="number" class="form-control" id="customMinutes" min="0" max="59" step="5" placeholder="分">
            <span>分</span>
            <button class="btn btn-primary btn-sm" onclick="applyCustomDuration()">適用</button>
        </div>
    `;

    const timeControls = document.querySelector('.time-adjust-controls');
    timeControls.appendChild(customTimeInput);
}

function applyCustomDuration() {
    const hours = parseInt(document.getElementById('customHours').value) || 0;
    const minutes = parseInt(document.getElementById('customMinutes').value) || 0;
    const totalMinutes = (hours * 60) + minutes;

    if (totalMinutes > 0) {
        const startTime = document.getElementById('modalStartTime').value;
        const endTime = calculateEndTime(startTime, totalMinutes);
        document.getElementById('modalEndTime').value = endTime;

        // カスタム入力フォームを閉じる
        document.querySelector('.custom-time-input').remove();
    }
}

function validateTimeInput() {
    const startTime = document.getElementById('modalStartTime').value;
    const endTime = document.getElementById('modalEndTime').value;
    const validationResult = {
        isValid: true,
        message: ''
    };

    // 営業時間内かチェック（6:00-23:30）
    const [startHour] = startTime.split(':').map(Number);
    const [endHour] = endTime.split(':').map(Number);

    if (startHour < 6 || endHour >= 24) {
        validationResult.isValid = false;
        validationResult.message = '設定可能な時間は6:00から23:30までです';
        return validationResult;
    }

    // 開始時間が終了時間より後でないかチェック
    const startDate = new Date(`2000-01-01T${startTime}`);
    const endDate = new Date(`2000-01-01T${endTime}`);

    if (startDate >= endDate) {
        validationResult.isValid = false;
        validationResult.message = '開始時間は終了時間より前である必要があります';
        return validationResult;
    }

    return validationResult;
}

function enhanceTimeInputFields() {
    const timeInputs = document.querySelectorAll('input[type="time"]');

    timeInputs.forEach(input => {
        // 時間ステッパーの追加
        const stepper = document.createElement('div');
        stepper.className = 'time-stepper';
        stepper.innerHTML = `
            <button type="button" class="time-step-btn" onclick="stepTime(this, -15)">▼</button>
            <button type="button" class="time-step-btn" onclick="stepTime(this, 15)">▲</button>
        `;
        input.parentNode.appendChild(stepper);

        // 入力補助表示の追加
        input.addEventListener('focus', function () {
            showTimeHelper(this);
        });
    });
}

function stepTime(button, minutes) {
    const input = button.closest('.time-input-group').querySelector('input[type="time"]');
    const currentTime = new Date(`2000-01-01T${input.value}`);
    currentTime.setMinutes(currentTime.getMinutes() + minutes);
    input.value = currentTime.toTimeString().slice(0, 5);

    validateAndUpdateTimes();
}

function showTimeHelper(input) {
    const helper = document.createElement('div');
    helper.className = 'time-helper';

    // よく使う時間帯のクイック選択ボタンを生成
    const quickTimes = generateQuickTimeButtons();
    helper.appendChild(quickTimes);

    // 現在の時間をハイライト
    highlightCurrentTime(helper);

    // ヘルパーを表示
    input.parentNode.appendChild(helper);

    // 外側をクリックしたら閉じる
    document.addEventListener('click', function closeHelper(e) {
        if (!helper.contains(e.target) && e.target !== input) {
            helper.remove();
            document.removeEventListener('click', closeHelper);
        }
    });
}

function generateQuickTimeButtons() {
    const container = document.createElement('div');
    container.className = 'quick-time-select';

    // 営業時間内（6:00-23:30）の主要な時間帯をボタンとして生成
    const hours = Array.from({ length: 18 }, (_, i) => i + 6);
    const minutes = ['00', '15', '30', '45'];

    hours.forEach(hour => {
        minutes.forEach(minute => {
            const timeBtn = document.createElement('button');
            timeBtn.className = 'quick-time-btn';
            timeBtn.textContent = `${hour}:${minute}`;
            timeBtn.onclick = () => selectQuickTime(timeBtn.textContent);
            container.appendChild(timeBtn);
        });
    });

    return container;
}

function selectQuickTime(time) {
    const activeInput = document.activeElement;
    activeInput.value = time;

    // 開始時間が選択された場合、終了時間を自動設定
    if (activeInput.id === 'modalStartTime') {
        const endTime = calculateEndTime(time, 30); // デフォルトで30分
        document.getElementById('modalEndTime').value = endTime;
    }

    validateAndUpdateTimes();
}

function highlightCurrentTime(helper) {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = Math.floor(now.getMinutes() / 15) * 15;

    const currentTimeBtn = helper.querySelector(
        `.quick-time-btn[data-time="${currentHour}:${currentMinute.toString().padStart(2, '0')}"]`
    );

    if (currentTimeBtn) {
        currentTimeBtn.classList.add('current-time');
    }
}

function validateAndUpdateTimes() {
    const validation = validateTimeInput();
    const errorDisplay = document.getElementById('timeErrorDisplay');

    if (!validation.isValid) {
        if (!errorDisplay) {
            showErrorMessage(validation.message);
        }
        return false;
    }

    if (errorDisplay) {
        errorDisplay.remove();
    }

    return true;
}

function updateDurationDisplay() {
    const startTime = document.getElementById('modalStartTime').value;
    const endTime = document.getElementById('modalEndTime').value;

    if (startTime && endTime) {
        const duration = calculateDuration(startTime, endTime);
        const durationDisplay = document.createElement('div');
        durationDisplay.className = 'time-duration-display';
        durationDisplay.innerHTML = `
            <span class="duration-icon">⏱</span>
            <span class="duration-text">${formatDuration(duration)}</span>
        `;

        // 既存の表示があれば更新
        const existingDisplay = document.querySelector('.time-duration-display');
        if (existingDisplay) {
            existingDisplay.replaceWith(durationDisplay);
        } else {
            document.querySelector('.time-input-group').appendChild(durationDisplay);
        }
    }
}

function calculateDuration(start, end) {
    const startDate = new Date(`2000-01-01T${start}`);
    const endDate = new Date(`2000-01-01T${end}`);
    return (endDate - startDate) / (1000 * 60); // 分単位で返す
}

function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours > 0 ? `${hours}時間` : ''}${mins > 0 ? `${mins}分` : ''}`;
}

function showTimeRangeOnTimeline() {
    const startTime = document.getElementById('modalStartTime').value;
    const endTime = document.getElementById('modalEndTime').value;

    // 既存のハイライトをクリア
    clearTimelineHighlight();

    if (startTime && endTime) {
        const timeSlots = document.querySelectorAll('.time-slot');
        timeSlots.forEach(slot => {
            const slotTime = slot.querySelector('.time-column').textContent;
            if (isTimeInRange(slotTime, startTime, endTime)) {
                slot.classList.add('time-range-highlight');
            }
        });
    }
}

function isTimeInRange(slotTime, startTime, endTime) {
    const slot = new Date(`2000-01-01T${slotTime}`);
    const start = new Date(`2000-01-01T${startTime}`);
    const end = new Date(`2000-01-01T${endTime}`);
    return slot >= start && slot <= end;
}

function clearTimelineHighlight() {
    document.querySelectorAll('.time-range-highlight').forEach(slot => {
        slot.classList.remove('time-range-highlight');
    });
}

function enhanceTimeRangeDisplay() {
    const timeSlots = document.querySelectorAll('.time-slot');
    const startTime = document.getElementById('modalStartTime').value;
    const endTime = document.getElementById('modalEndTime').value;

    timeSlots.forEach(slot => {
        const slotTime = slot.querySelector('.time-column').textContent;

        if (slotTime === startTime) {
            slot.classList.add('time-range-start');
            addTimeIndicator(slot, '開始');
        }

        if (slotTime === endTime) {
            slot.classList.add('time-range-end');
            addTimeIndicator(slot, '終了');
        }
    });
}

function addTimeIndicator(slot, type) {
    const indicator = document.createElement('div');
    indicator.className = `time-indicator ${type === '開始' ? 'start' : 'end'}`;
    indicator.innerHTML = `
        <span class="indicator-label">${type}</span>
        <span class="indicator-time">${slot.querySelector('.time-column').textContent}</span>
    `;
    slot.querySelector('.task-column').appendChild(indicator);
}

function enhanceTimeRangeInteraction() {
    // ドラッグによる時間調整
    const timeSlots = document.querySelectorAll('.time-slot');
    let isDragging = false;
    let startSlot = null;
    let endSlot = null;

    timeSlots.forEach(slot => {
        slot.addEventListener('mousedown', (e) => {
            isDragging = true;
            startSlot = slot;
            updateTimeSelection(startSlot, startSlot);
        });

        slot.addEventListener('mousemove', (e) => {
            if (isDragging) {
                endSlot = slot;
                updateTimeSelection(startSlot, endSlot);
            }
        });
    });

    document.addEventListener('mouseup', () => {
        if (isDragging && startSlot && endSlot) {
            applyTimeSelection(startSlot, endSlot);
        }
        isDragging = false;
    });
}

function updateTimeSelection(start, end) {
    clearTimelineHighlight();
    const startTime = start.querySelector('.time-column').textContent;
    const endTime = end.querySelector('.time-column').textContent;

    if (new Date(`2000-01-01T${startTime}`) > new Date(`2000-01-01T${endTime}`)) {
        [start, end] = [end, start];
    }

    highlightTimeRange(start, end);
}

function highlightTimeRange(start, end) {
    const timeSlots = document.querySelectorAll('.time-slot');
    let isInRange = false;

    timeSlots.forEach(slot => {
        if (slot === start) isInRange = true;
        if (isInRange) slot.classList.add('time-range-highlight');
        if (slot === end) isInRange = false;
    });
}

function enhanceVisualFeedback() {
    // ホバー時のプレビュー表示
    const timeSlots = document.querySelectorAll('.time-slot');

    timeSlots.forEach(slot => {
        slot.addEventListener('mouseenter', (e) => {
            if (!slot.classList.contains('time-range-highlight')) {
                showTimePreview(slot);
            }
        });
    });
}

function showTimePreview(slot) {
    const preview = document.createElement('div');
    preview.className = 'time-preview';
    const time = slot.querySelector('.time-column').textContent;

    preview.innerHTML = `
        <div class="preview-content">
            <div class="preview-time">${time}</div>
            <div class="preview-duration">30分</div>
            <div class="preview-hint">クリックして選択</div>
        </div>
    `;

    slot.querySelector('.task-column').appendChild(preview);

    // プレビューのクリーンアップ
    slot.addEventListener('mouseleave', () => {
        preview.remove();
    });
}

function enhanceTimeSelectionPrecision() {
    const timeControls = document.createElement('div');
    timeControls.className = 'precision-controls';
    timeControls.innerHTML = `
        <div class="precision-slider">
            <label>時間の細かさ</label>
            <input type="range" min="5" max="30" step="5" value="15" class="precision-input">
            <span class="precision-value">15分</span>
        </div>
        <div class="precision-buttons">
            <button class="micro-adjust" data-adjust="-5">-5分</button>
            <button class="micro-adjust" data-adjust="+5">+5分</button>
        </div>
    `;

    // 時間の微調整
    timeControls.querySelectorAll('.micro-adjust').forEach(button => {
        button.addEventListener('click', () => {
            const adjustment = parseInt(button.dataset.adjust);
            adjustTimeWithPrecision(adjustment);
        });
    });

    // スライダーによる時間間隔の調整
    const slider = timeControls.querySelector('.precision-input');
    slider.addEventListener('input', (e) => {
        const value = e.target.value;
        timeControls.querySelector('.precision-value').textContent = `${value}分`;
        updateTimeGridPrecision(value);
    });

    return timeControls;
}

function updateTimeGridPrecision(precision) {
    const timeGrid = document.querySelector('.timeline-grid');
    const startHour = 6;  // 6:00開始
    const endHour = 24;   // 24:00終了

    // グリッドをクリア
    timeGrid.innerHTML = '';

    // 新しい精度でグリッドを生成
    for (let hour = startHour; hour < endHour; hour++) {
        const intervals = 60 / precision;
        for (let i = 0; i < intervals; i++) {
            const minutes = i * precision;
            const timeSlot = createPreciseTimeSlot(hour, minutes);
            timeGrid.appendChild(timeSlot);
        }
    }

    // イベントリスナーを再設定
    initializeTimeSlotListeners();
}

function createPreciseTimeSlot(hour, minutes) {
    const timeSlot = document.createElement('div');
    timeSlot.className = 'time-slot';

    const formattedTime = `${hour.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;

    timeSlot.innerHTML = `
        <div class="time-column">${formattedTime}</div>
        <div class="task-column" data-time="${formattedTime}"></div>
    `;

    return timeSlot;
}

function adjustTaskDisplayPrecision() {
    const tasks = document.querySelectorAll('.task-item');
    const precision = parseInt(document.querySelector('.precision-input').value);

    tasks.forEach(task => {
        const startTime = task.dataset.startTime;
        const endTime = task.dataset.endTime;

        // 時間を精度に合わせて調整
        const adjustedStart = roundTimeToInterval(startTime, precision);
        const adjustedEnd = roundTimeToInterval(endTime, precision);

        // タスクの表示位置とサイズを更新
        updateTaskPosition(task, adjustedStart, adjustedEnd);
    });
}

function roundTimeToInterval(time, interval) {
    const [hours, minutes] = time.split(':').map(Number);
    const totalMinutes = hours * 60 + minutes;
    const roundedMinutes = Math.round(totalMinutes / interval) * interval;

    const newHours = Math.floor(roundedMinutes / 60);
    const newMinutes = roundedMinutes % 60;

    return `${newHours.toString().padStart(2, '0')}:${newMinutes.toString().padStart(2, '0')}`;
}

function updateTaskPosition(task, startTime, endTime) {
    const container = task.closest('.task-column');
    const slotHeight = container.offsetHeight;

    // 時間に基づいて位置とサイズを計算
    const [startHour, startMinute] = startTime.split(':').map(Number);
    const [endHour, endMinute] = endTime.split(':').map(Number);

    const startOffset = (startHour - 6) * slotHeight + (startMinute / 60) * slotHeight;
    const duration = ((endHour - startHour) * 60 + (endMinute - startMinute)) / 60 * slotHeight;

    task.style.top = `${startOffset}px`;
    task.style.height = `${duration}px`;
}

function enhanceTaskVisuals() {
    const tasks = document.querySelectorAll('.task-item');

    tasks.forEach(task => {
        // タスクの優先度に基づく視覚的表現
        const priority = task.dataset.priority;
        applyPriorityStyles(task, priority);

        // タスクの進行状況バーを追加
        addProgressBar(task);

        // タスクの所要時間を表示
        addDurationBadge(task);

        // ホバー時の詳細情報を追加
        addHoverDetails(task);
    });
}

function applyPriorityStyles(task, priority) {
    task.classList.add(`priority-${priority}`);

    const priorityIndicator = document.createElement('div');
    priorityIndicator.className = 'priority-indicator';
    priorityIndicator.innerHTML = getPriorityIcon(priority);
    task.insertBefore(priorityIndicator, task.firstChild);
}

function addProgressBar(task) {
    const progress = document.createElement('div');
    progress.className = 'task-progress';
    progress.innerHTML = `
        <div class="progress-bar" style="width: ${task.dataset.progress}%"></div>
    `;
    task.appendChild(progress);
}

function addQuickActions(task) {
    const quickActions = document.createElement('div');
    quickActions.className = 'quick-actions';

    quickActions.innerHTML = `
        <button class="quick-action-btn edit-btn" onclick="quickEdit(event, ${task.dataset.taskId})">
            <i class="fas fa-edit"></i> 編集
        </button>
        <button class="quick-action-btn status-btn" onclick="quickStatusChange(event, ${task.dataset.taskId})">
            <i class="fas fa-check"></i> 状態変更
        </button>
        <button class="quick-action-btn delete-btn" onclick="quickDelete(event, ${task.dataset.taskId})">
            <i class="fas fa-trash"></i> 削除
        </button>
    `;

    task.appendChild(quickActions);
}

function quickEdit(event, taskId) {
    event.stopPropagation();
    const task = document.querySelector(`[data-task-id="${taskId}"]`);

    const editForm = createQuickEditForm(task);
    task.appendChild(editForm);
}

function createQuickEditForm(task) {
    const form = document.createElement('div');
    form.className = 'quick-edit-form';
    form.innerHTML = `
        <input type="text" class="quick-edit-title" value="${task.dataset.title}">
        <select class="quick-edit-priority">
            <option value="1">至急！</option>
            <option value="2">高</option>
            <option value="3">中</option>
            <option value="4">低</option>
            <option value="5">未設定</option>
        </select>
        <div class="quick-edit-buttons">
            <button onclick="saveQuickEdit(event, ${task.dataset.taskId})">保存</button>
            <button onclick="cancelQuickEdit(event)">キャンセル</button>
        </div>
    `;

    return form;
}

function quickStatusChange(event, taskId) {
    event.stopPropagation();

    const statusMenu = document.createElement('div');
    statusMenu.className = 'status-menu';
    statusMenu.innerHTML = `
        <div class="status-option" data-status="1">
            <span class="status-dot status-not-started"></span>未着手
        </div>
        <div class="status-option" data-status="2">
            <span class="status-dot status-in-review"></span>確認中
        </div>
        <div class="status-option" data-status="3">
            <span class="status-dot status-in-progress"></span>進行中
        </div>
        <div class="status-option" data-status="4">
            <span class="status-dot status-completed"></span>完了
        </div>
    `;

    const task = event.target.closest('.task-item');
    task.appendChild(statusMenu);

    // ステータス選択のイベントリスナー
    statusMenu.querySelectorAll('.status-option').forEach(option => {
        option.addEventListener('click', async () => {
            const newStatus = option.dataset.status;
            await updateTaskStatus(taskId, newStatus);
            refreshTaskDisplay(task, newStatus);
            statusMenu.remove();
        });
    });
}

async function updateTaskStatus(taskId, newStatus) {
    try {
        const response = await fetch(`/api/v1/tasks/${taskId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            throw new Error('ステータスの更新に失敗しました');
        }

        return await response.json();
    } catch (error) {
        showErrorMessage(error.message);
        throw error;
    }
}

function refreshTaskDisplay(task, newStatus) {
    // 古いステータスクラスを削除
    task.classList.remove('status-not-started', 'status-in-review', 'status-in-progress', 'status-completed');

    // 新しいステータスクラスを追加
    const statusClass = getStatusClass(newStatus);
    task.classList.add(statusClass);

    // ステータスバッジを更新
    const statusBadge = task.querySelector('.status-badge');
    statusBadge.textContent = getStatusText(newStatus);
    statusBadge.className = `status-badge ${statusClass}`;

    // アニメーション効果
    task.classList.add('status-updated');
    setTimeout(() => task.classList.remove('status-updated'), 1000);
}

function showStatusNotification(status) {
    const notification = document.createElement('div');
    notification.className = 'status-notification';

    const statusInfo = {
        1: { text: '未着手', color: '#6c757d' },
        2: { text: '確認中', color: '#ffc107' },
        3: { text: '進行中', color: '#17a2b8' },
        4: { text: '完了', color: '#28a745' }
    };

    notification.innerHTML = `
        <div class="notification-content" style="border-left: 4px solid ${statusInfo[status].color}">
            <div class="notification-icon">
                <i class="fas fa-info-circle"></i>
            </div>
            <div class="notification-text">
                <p>ステータスを「${statusInfo[status].text}」に更新しました</p>
            </div>
            <div class="notification-progress"></div>
        </div>
    `;

    document.body.appendChild(notification);

    // 3秒後に通知を消す
    setTimeout(() => {
        notification.classList.add('notification-hide');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

class NotificationManager {
    constructor() {
        this.notifications = [];
        this.container = this.createContainer();
    }

    createContainer() {
        const container = document.createElement('div');
        container.className = 'notification-container';
        document.body.appendChild(container);
        return container;
    }

    show(message, type = 'info') {
        const notification = {
            id: Date.now(),
            message,
            type,
            element: this.createNotificationElement(message, type)
        };

        this.notifications.push(notification);
        this.container.appendChild(notification.element);
        this.animateIn(notification);

        // スタック表示の調整
        this.adjustStack();

        // 自動消去
        setTimeout(() => {
            this.dismiss(notification.id);
        }, 3000);

        return notification.id;
    }

    createNotificationElement(message, type) {
        const element = document.createElement('div');
        element.className = `notification notification-${type}`;
        element.innerHTML = `
            <div class="notification-content">
                <i class="notification-icon ${this.getIconClass(type)}"></i>
                <span class="notification-message">${message}</span>
                <button class="notification-close" onclick="notificationManager.dismiss(${Date.now()})">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="notification-progress"></div>
        `;
        return element;
    }
}

