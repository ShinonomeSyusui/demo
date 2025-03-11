// タスク一覧を取得する関数
// タスクをロードする非同期関数
async function loadTasks(page, year, month, usersName, showAll) {
    // APIエンドポイントからタスクデータをフェッチ
    const response = await fetch(`/api/v1/tasks/page?page=${page}&year=${year}&month=${month}&usersName=${usersName}&showAll=${showAll}`);

    // レスポンスをJSON形式でパース
    const data = await response.json();

    // 取得したデータを使ってタスクテーブルを更新
    updateTaskTable(data);
}


// タスクテーブルを更新する関数
function updateTaskTable(data) {
    // テーブル更新のロジック
}
