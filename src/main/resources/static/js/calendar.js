function showTaskDetail(element) {
    const taskId = element.getAttribute('data-task-id');
    const title = element.getAttribute('data-task-title');
    const description = element.getAttribute('data-task-description');
    const status = element.getAttribute('data-task-status');
    const dueDateStr = element.getAttribute('data-task-due-date');

    const dueDate = new Date(dueDateStr);
    const weekDays = ['日', '月', '火', '水', '木', '金', '土'];
    const year = dueDate.getFullYear();
    const month = dueDate.getMonth() + 1;
    const day = dueDate.getDate();
    const weekDay = weekDays[dueDate.getDay()];

    const formattedDate = `${year}年${month}月${day}日(${weekDay})`;

    document.getElementById('taskTitle').textContent = title;
    document.getElementById('taskDescription').textContent = description;
    document.getElementById('taskStatus').textContent = status;
    document.getElementById('taskDueDate').textContent = formattedDate;

    const modal = new bootstrap.Modal(document.getElementById('taskDetailModal'));
    modal.show();
}

function editTask() {
    const year = document.getElementById('selectedYear').value;
    const month = document.getElementById('selectedMonth').value;
    window.location.href = `/createTaskPage/edit?taskId=${currentTaskId}`;
}


function deleteTask() {
    if (confirm('このタスクを削除してもよろしいですか？')) {
        const year = document.getElementById('selectedYear').value;
        const month = document.getElementById('selectedMonth').value;

        fetch(`/taskChange`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=3&taskId=${currentTaskId}&year=${year}&month=${month}`
        }).then(() => {
            window.location.reload();
        });
    }
}
