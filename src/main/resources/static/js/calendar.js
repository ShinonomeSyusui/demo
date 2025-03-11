function showTaskDetail(element) {
    // タスクIDを保存
    currentTaskId = element.getAttribute('data-task-id');

    // タイトルを取得
    const title = element.getAttribute('data-task-title');

    // 説明を取得
    const description = element.getAttribute('data-task-description');

    // ステータスを取得
    const status = element.getAttribute('data-task-status');

    // 期日を文字列として取得
    const dueDateStr = element.getAttribute('data-task-due-date');

    // 期日をDateオブジェクトに変換
    const dueDate = new Date(dueDateStr);

    // 曜日の配列を定義
    const weekDays = ['日', '月', '火', '水', '木', '金', '土'];

    // 年を取得
    const year = dueDate.getFullYear();

    // 月を取得（0始まりのため+1）
    const month = dueDate.getMonth() + 1;

    // 日を取得
    const day = dueDate.getDate();

    // 曜日を取得
    const weekDay = weekDays[dueDate.getDay()];

    // フォーマットされた日付を作成
    const formattedDate = `${year}年${month}月${day}日(${weekDay})`;

    // 優先度情報を取得
    const priority = element.getAttribute('data-task-priority');

    // タイトルを表示
    document.getElementById('taskTitle').textContent = title;

    // 説明を表示
    document.getElementById('taskDescription').textContent = description;

    // ステータスを表示
    document.getElementById('taskStatus').textContent = status;

    // 期日を表示
    document.getElementById('taskDueDate').textContent = formattedDate;

    // 優先度セレクトボックスの値を設定
    document.getElementById('taskPriority').value = priority;

    // モーダルを取得し表示
    const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
    modal.show();

    // ステータス変更ボタンのクリックイベント
    document.getElementById('changeStatusBtn').addEventListener('click', async () => {
        const status = document.getElementById('statusSelect').value;
        try {
            const result = await updateTaskStatus(currentTaskId, status);
            if (result.success) {
                window.location.reload();
            }
        } catch (error) {
            console.error('ステータス更新エラー:', error);
        }
    });

    // タスク完了ボタンのクリックイベント
    document.getElementById('completeTaskBtn').addEventListener('click', async () => {
        try {
            const result = await markTaskComplete(currentTaskId);
            if (result.success) {
                window.location.reload();
            }
        } catch (error) {
            console.error('タスク完了エラー:', error);
        }
    });

}


function editTask() {
    // 選択された年を取得
    const year = document.getElementById('selectedYear').value;
    // 選択された月を取得
    const month = document.getElementById('selectedMonth').value;
    // 保存されたタスクIDを使用してページを移動
    window.location.href = `/createTaskPage/edit?taskId=${currentTaskId}&year=${year}&month=${month}`;
}




function deleteTask() {
    // タスク削除の確認ダイアログを表示し、ユーザーの承認を得る
    if (confirm('このタスクを削除してもよろしいですか？')) {

        // 選択された年を取得
        const year = document.getElementById('selectedYear').value;

        // 選択された月を取得
        const month = document.getElementById('selectedMonth').value;

        // サーバーへタスク削除リクエストを送信
        fetch(`/taskChange`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            // リクエストボディに必要なデータを含める
            body: `action=3&taskId=${currentTaskId}&year=${year}&month=${month}`
        }).then(() => {
            // ページを再読み込みして変更を反映
            window.location.reload();
        });
    }
}


function handleDragStart(event) {
    // ドラッグ開始時にデータを設定する
    // 'text/plain' 形式で、ドラッグされた要素の 'data-task-id' 属性値を保存
    event.dataTransfer.setData('text/plain', event.target.getAttribute('data-task-id'));
}


function handleDragOver(event) {
    // ドラッグオーバー時のデフォルトの動作を無効にする
    // これにより、要素がドロップ可能になる
    event.preventDefault();
}


function handleDrop(event) {
    event.preventDefault(); // デフォルトのドラッグ&ドロップ動作を無効にする
    const taskId = event.dataTransfer.getData('text/plain'); // ドラッグされたタスクIDを取得
    const cell = event.target.closest('td'); // ドロップ先のセルを取得
    const dateElement = cell.querySelector('.date'); // セル内の日付要素を取得
    // trim()を使用して余分な空白を除去
    const dropDate = dateElement.textContent.trim(); // ドロップされた日付を取得

    const year = document.getElementById('selectedYear').value; // 選択された年を取得
    const month = document.getElementById('selectedMonth').value; // 選択された月を取得

    // タスクの新しい日付をサーバーに送信
    fetch('/editTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            taskId: taskId, // タスクIDを設定
            year: year, // 年を設定
            month: month, // 月を設定
            day: dropDate // 日を設定
        })
    })
        .then(response => response.json()) // レスポンスをJSON形式で処理
        .then(data => {
            if (data.success) {
                window.location.reload(); // 更新が成功したらページをリロード
            }
        });
}
function handleDrop(event) {
    event.preventDefault(); // デフォルトのドラッグ&ドロップ動作を無効にする
    const taskId = event.dataTransfer.getData('text/plain'); // ドラッグされたタスクIDを取得
    const cell = event.target.closest('td'); // ドロップ先のセルを取得
    const dateElement = cell.querySelector('.date'); // セル内の日付要素を取得
    // trim()を使用して余分な空白を除去
    const dropDate = dateElement.textContent.trim(); // ドロップされた日付を取得

    const year = document.getElementById('selectedYear').value; // 選択された年を取得
    const month = document.getElementById('selectedMonth').value; // 選択された月を取得

    // タスクの新しい日付をサーバーに送信
    fetch('/editTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            taskId: taskId, // タスクIDを設定
            year: year, // 年を設定
            month: month, // 月を設定
            day: dropDate // 日を設定
        })
    })
        .then(response => response.json()) // レスポンスをJSON形式で処理
        .then(data => {
            if (data.success) {
                window.location.reload(); // 更新が成功したらページをリロード
            }
        });
}




function updateTaskDate(taskId, year, month, day) {
    // タスクの元の情報を取得
    const taskElement = document.querySelector(`[data-task-id="${taskId}"]`);
    const title = taskElement.getAttribute('data-task-title');
    const description = taskElement.getAttribute('data-task-description');
    const status = taskElement.getAttribute('data-task-status');
    const priority = taskElement.getAttribute('data-task-priority');

    // 日付をフォーマット
    const formattedMonth = month.padStart(2, '0');
    const formattedDay = day.padStart(2, '0');
    const newDate = `${year}-${formattedMonth}-${formattedDay}`;

    // タスクの情報をサーバーに送信して更新
    fetch('/createTaskPage/edit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `id=${taskId}&title=${title}&description=${description}&status=${status}&dueDate=${newDate}&priority=${priority}`
    }).then(() => {
        // 更新後、ページをリロード
        window.location.reload();
    });
}

function openNewTaskModal(event) {
    // クリックされたセルを取得
    const cell = event.target.closest('td');

    // セル内の日付を取得して整形
    const date = cell.querySelector('.date').textContent.trim();

    // 選択された年と月を取得
    const year = document.getElementById('selectedYear').value;
    const month = document.getElementById('selectedMonth').value;

    // 選択された日付をフォーマット（YYYY-MM-DD形式）
    const selectedDate = `${year}-${month.padStart(2, '0')}-${date.padStart(2, '0')}`;

    // フォーマットされた日付を新規タスクの日付フィールドに設定
    document.getElementById('newTaskDate').value = selectedDate;

    // モーダルを表示
    const modal = new bootstrap.Modal(document.getElementById('newTaskModal'));
    modal.show();
}


function submitNewTask() {
    // 選択された日付を取得
    const selectedDate = document.getElementById('newTaskDate').value;
    console.log("選択された日付:", selectedDate);

    // タスクデータの作成
    const taskData = {
        title: document.getElementById('title').value.trim(), // タイトルを取得し、空白をトリム
        description: document.getElementById('description').value.trim(), // 説明を取得し、空白をトリム
        status: "1", // ステータスを設定
        dueDate: selectedDate, // 期日を設定
        priority: document.getElementById('priority').value, // 優先度を取得
        userId: 1 // ユーザーIDを設定
    };

    console.log("送信するデータ:", taskData);

    // サーバーにタスクデータを送信
    fetch('/createTaskPage/createFromCalendar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(taskData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok'); // エラーがある場合は例外をスロー
            }
            const modal = bootstrap.Modal.getInstance(document.getElementById('newTaskModal'));
            modal.hide(); // モーダルを非表示にする
            window.location.reload(); // ページをリロードする
        })
        .catch(error => {
            console.error('Error:', error); // エラーをコンソールに出力
        });
}

async function updateTaskStatus(taskId, status) {
    // タスクのステータスを更新するためのAPIリクエストを送信
    const response = await fetch(`/api/v1/tasks/${taskId}/status?status=${status}`, {
        method: 'PUT', // HTTPメソッドはPUTを使用
        headers: {
            'Content-Type': 'application/json' // リクエストのコンテンツタイプをJSONに設定
        }
    });
    // レスポンスをJSON形式で返す
    return await response.json();
}



async function markTaskComplete(taskId) {
    // タスクを完了としてマークするためのAPIリクエストを送信
    const response = await fetch(`/api/v1/tasks/${taskId}/complete`, {
        method: 'PUT', // HTTPメソッドはPUTを使用
        headers: {
            'Content-Type': 'application/json' // リクエストのコンテンツタイプをJSONに設定
        }
    });
    // レスポンスをJSON形式で返す
    return await response.json();
}


async function deleteTask(taskId) {
    // 指定されたタスクを削除するためのAPIリクエストを送信
    const response = await fetch(`/api/v1/tasks/${taskId}`, {
        method: 'DELETE' // HTTPメソッドはDELETEを使用
    });
    // レスポンスをJSON形式で返す
    return await response.json();
}

// エラーメッセージを表示する共通関数
function showErrorMessage(message) {
    // エラーメッセージを表示する要素を取得
    const errorDiv = document.getElementById('errorMessage');
    // 指定されたメッセージを要素に設定
    errorDiv.textContent = message;
    // 要素を表示状態にする
    errorDiv.style.display = 'block';
    // 3秒後にエラーメッセージを非表示にするタイマーをセット
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 3000);
}


// APIレスポンスを処理する共通関数
async function handleApiResponse(response) {
    // レスポンスからJSONデータを非同期で取得
    const data = await response.json();
    // レスポンスが正常でない場合、エラーをスロー
    if (!response.ok) {
        // データ内のエラーメッセージがあればそれを使用し、なければデフォルトメッセージを使用
        throw new Error(data.error || 'エラーが発生しました');
    }
    // 正常なデータを返す
    return data;
}


// 既存の関数を更新
async function updateTaskStatus(taskId, status) {
    try {
        // 指定されたタスクIDとステータスでAPIリクエストを送信
        const response = await fetch(`/api/v1/tasks/${taskId}/status?status=${status}`, {
            method: 'PUT', // HTTPメソッドはPUTを使用
            headers: {
                'Content-Type': 'application/json' // リクエストヘッダーにコンテンツタイプを指定
            }
        });
        // APIレスポンスを処理する共通関数を呼び出し、結果を取得
        const result = await handleApiResponse(response);
        // 結果を返す
        return result;
    } catch (error) {
        // エラーメッセージを表示する関数を呼び出し
        showErrorMessage(error.message);
        // エラーを再スロー
        throw error;
    }
}

// 一括ステータス更新関数
async function updateBatchTaskStatus(requests) {
    try {
        // APIに対して非同期でPUTリクエストを送信
        const response = await fetch('/api/v1/tasks/batch/status', {
            method: 'PUT', // HTTPメソッドを指定
            headers: {
                'Content-Type': 'application/json' // リクエストのペイロードがJSON形式であることを指定
            },
            body: JSON.stringify(requests) // リクエストボディにタスクのステータス情報をJSONとして送信
        });

        // APIレスポンスを処理して結果を返す
        return await handleApiResponse(response);
    } catch (error) {
        // エラーメッセージを表示
        showErrorMessage(error.message);

        // エラーを再スロー
        throw error;
    }
}


// 一括削除関数
async function deleteBatchTasks(taskIds) {
    try {
        // APIに対して非同期でDELETEリクエストを送信
        const response = await fetch('/api/v1/tasks/batch', {
            method: 'DELETE', // HTTPメソッドを指定
            headers: {
                'Content-Type': 'application/json' // リクエストのペイロードがJSON形式であることを指定
            },
            body: JSON.stringify(taskIds) // リクエストボディに削除するタスクIDの配列をJSONとして送信
        });

        // APIレスポンスを処理して結果を返す
        return await handleApiResponse(response);
    } catch (error) {
        // エラーメッセージを表示
        showErrorMessage(error.message);

        // エラーを再スロー
        throw error;
    }
}

// タスク選択の状態管理
let selectedTasks = new Set();

// チェックボックスの切り替え処理
function toggleTaskSelection(taskId) {
    // 指定されたタスクIDに対応するチェックボックス要素を取得
    const checkbox = document.getElementById(`task-checkbox-${taskId}`);

    // チェックボックスがチェックされているかどうかを判定
    if (checkbox.checked) {
        // チェックされている場合、選択されたタスクの集合にタスクIDを追加
        selectedTasks.add(taskId);
    } else {
        // チェックされていない場合、選択されたタスクの集合からタスクIDを削除
        selectedTasks.delete(taskId);
    }

    // 一括操作ボタンの状態を更新
    updateBatchOperationButtons();
}


// 一括操作ボタンの表示制御
function updateBatchOperationButtons() {
    // 一括操作ボタンの要素を取得
    const batchButtons = document.getElementById('batch-operation-buttons');

    // 選択されたタスクが1つ以上ある場合
    if (selectedTasks.size > 0) {
        // ボタンを表示する
        batchButtons.style.display = 'block';
    } else {
        // 選択されたタスクがない場合、ボタンを非表示にする
        batchButtons.style.display = 'none';
    }
}


// 一括ステータス更新の実行
async function executeBatchStatusUpdate(status) {
    // 選択されたタスクをリクエスト用に整形
    const requests = Array.from(selectedTasks).map(taskId => ({
        taskId: taskId, // タスクIDを設定
        status: status  // 更新するステータスを設定
    }));

    // ステータス更新のリクエストを送信し、結果を待つ
    const result = await updateBatchTaskStatus(requests);

    // 更新が成功した場合
    if (result.success) {
        // ページを再読み込みして変更を反映
        window.location.reload();
    }
}


// 一括削除の実行
async function executeBatchDelete() {
    // ユーザーに確認ダイアログを表示し、削除するかどうか確認
    if (confirm('選択したタスクを一括削除してもよろしいですか？')) {
        // 選択されたタスクを削除する非同期処理を実行
        const result = await deleteBatchTasks(Array.from(selectedTasks));

        // 削除が成功した場合
        if (result.success) {
            // ページを再読み込みして変更を反映
            window.location.reload();
        }
    }
}

// 単一タスクの優先度更新
async function updateTaskPriority(taskId, priority) {
    try {
        const response = await fetch(`/api/v1/tasks/priority/${taskId}?priority=${priority}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        console.log('API Response:', response);  // レスポンスの確認用
        return response;
    } catch (error) {
        console.error('API Error:', error);  // エラーの確認用
        throw error;
    }
}

// 複数タスクの優先度一括更新
async function updateBatchPriorities(requests) {
    try {
        const response = await fetch('/api/v1/tasks/priority/batch', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requests)
        });
        return await handleApiResponse(response);
    } catch (error) {
        showErrorMessage(error.message);
        throw error;
    }
}

// 優先度変更のハンドラー関数
async function handlePriorityChange(event) {
    const priority = event.target.value;
    try {
        const response = await updateTaskPriority(currentTaskId, priority);
        console.log('Response status:', response.status);  // ステータスコードの確認用

        const taskElement = document.querySelector(`[data-task-id="${currentTaskId}"]`);
        taskElement.setAttribute('data-task-priority', priority);
        updateTaskDisplay(taskElement, priority);

        // 成功時の処理のみを行う
        showSuccessMessage('優先度を更新しました');

    } catch (error) {
        console.error('Handler Error:', error);  // エラーの確認用
    }
}


// タスク表示の更新
function updateTaskDisplay(element, priority) {
    // 既存のクラスを削除
    element.classList.remove('priority-1', 'priority-2', 'priority-3', 'priority-4', 'priority-5');
    // 新しい優先度のクラスを追加
    element.classList.add(`priority-${priority}`);
}

// 成功メッセージの表示
function showSuccessMessage(message) {
    const messageDiv = document.getElementById('successMessage');
    messageDiv.textContent = message;
    messageDiv.style.display = 'block';
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 3000);
}

function filterTasksByPriority(priority) {
    const taskItems = document.querySelectorAll('.task-item span');

    taskItems.forEach(task => {
        const taskPriority = task.getAttribute('data-task-priority');
        const taskElement = task.closest('.task-item');

        if (priority === "0" || taskPriority === priority) {
            taskElement.style.display = '';
        } else {
            taskElement.style.display = 'none';
        }
    });

    // フィルター状態を表示
    updateFilterStatus(priority);
}

function updateFilterStatus(priority) {
    const statusMap = {
        "0": "全ての優先度",
        "1": "至急！のタスク",
        "2": "優先度：高のタスク",
        "3": "優先度：中のタスク",
        "4": "優先度：低のタスク",
        "5": "未設定のタスク"
    };

    const filterStatus = document.createElement('div');
    filterStatus.textContent = `表示中: ${statusMap[priority]}`;
    filterStatus.className = 'filter-status mt-2';

    const existingStatus = document.querySelector('.filter-status');
    if (existingStatus) {
        existingStatus.remove();
    }

    document.getElementById('priorityFilter').parentNode.appendChild(filterStatus);
}



























