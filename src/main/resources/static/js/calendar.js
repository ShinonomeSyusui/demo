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

    // タイトルを表示
    document.getElementById('taskTitle').textContent = title;

    // 説明を表示
    document.getElementById('taskDescription').textContent = description;

    // ステータスを表示
    document.getElementById('taskStatus').textContent = status;

    // 期日を表示
    document.getElementById('taskDueDate').textContent = formattedDate;

    // モーダルを取得し表示
    const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
    modal.show();
}


function editTask() {
    // 保存されたタスクIDを使用
    window.location.href = `/createTaskPage/edit?taskId=${currentTaskId}`;
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
    // デフォルトの動作を無効にする
    event.preventDefault();

    // ドラッグされたタスクのIDを取得
    const taskId = event.dataTransfer.getData('text/plain');

    // ドロップ先のセルを取得
    const cell = event.target.closest('td');
    const dateElement = cell.querySelector('.date');
    const dropDate = dateElement.textContent;

    // 年月を取得
    const year = document.getElementById('selectedYear').value;
    const month = document.getElementById('selectedMonth').value;

    // タスク更新をサーバーに送信
    fetch('/editTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            taskId: taskId,
            year: year,
            month: month,
            day: dropDate
        })
    })
        .then(response => response.json())
        .then(data => {
            // 更新が成功した場合、ページをリロード
            if (data.success) {
                window.location.reload();
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

    // セル内の日付を取得
    const date = cell.querySelector('.date').textContent;

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
    // 新規タスクフォームを取得
    const form = document.getElementById('newTaskForm');

    // フォームデータを作成
    const formData = new FormData(form);

    // タスクを作成するためのリクエストを送信
    fetch('/createTask', {
        method: 'POST',
        body: formData
    }).then(() => {
        // ページをリロードして変更を反映
        window.location.reload();
    });
}

function submitNewTask() {
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const status = document.getElementById('status').value;
    const dueDate = document.getElementById('newTaskDate').value;

    fetch('/createTaskPage/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `title=${encodeURIComponent(title)}&description=${encodeURIComponent(description)}&status=${status}&dueDate=${dueDate}&userId=1&priority=1`
    }).then(response => {
        if (response.ok) {
            window.location.reload();
        }
    });
}


// 既存の登録エンドポイントにPOSTリクエストを送信
fetch('/createTaskPage/create', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
}).then(response => {
    if (response.ok) {
        window.location.reload();
    }
});






