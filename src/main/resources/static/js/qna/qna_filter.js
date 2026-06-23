document.getElementById('filter-status').addEventListener('change', function() {
    const selectedCategory = this.value;
    console.log("선택된 카테고리: ", selectedCategory);

    // 스프링 API 호출
    fetch(`/api/board/filter?category=${selectedCategory}`)
        .then(response => response.json())
        .then(data => {
            // 기존 목록을 비우고, 새로 받아온 데이터로 목록을 그려주는 로직
            const listContainer = document.getElementById('board-list-container');
            if (!listContainer) return;
            listContainer.innerHTML = '';

            // 데이터를 가져올 리스트 정의
            const boardList = data.list;

            // 데이터가 없다면...
            if (!boardList || boardList.length === 0) {
                listContainer.innerHTML = `<tr><td colspan="6" style="text-align:center;">게시글이 없습니다.</td></tr>`;
                return;
            }
            
            boardList.forEach(list => {

                const dateStr = list.createdAt ? list.createdAt.substring(0, 10).replace(/-/g, '.') : '';
                listContainer.innerHTML += `<tr>
                        <td>${list.boardId}</td>
                        <td>[${selectedCategory}]</td>
                        <td>${list.title}</td>
                        <td>${list.username}</td>
                        <td>${dateStr}</td>
                        <td>${list.viewCnt}</td>
                    </tr>`;
            });
        })
        .catch(error => console.error('Error:', error));
});