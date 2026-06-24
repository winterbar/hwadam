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

                // category = 

                const dateStr = list.createdAt ? list.createdAt.substring(0, 10).replace(/-/g, '.') : '';
                listContainer.innerHTML += `<tr>
                        <td class="col-num">${list.boardId}</td>
                        <td class="col-product" style="color: var(--color-accent); font-weight: 800;">[${list.category}]</td>
                        <td>
                            <a href="/board/qna/${list.boardId}" class="col-title">${list.title}</a>
                            <span class="reply-count">[${list.replyCnt}]</span>
                        </td>
                        <td class="col-writer">${list.nickname}</td>
                        <td class="col-date">${dateStr}</td>
                        <td class="col-hits">${list.viewCnt}</td>
                    </tr>`;
            });

            // 2. 🔥 페이지네이션 동적 렌더링 추가
            const paginationContainer = document.getElementById('pagination-container');
            if (!paginationContainer) return;
            paginationContainer.innerHTML = ''; // 기존 타임리프가 그린 페이지 번호 삭제

            const pageInfo = data.pageInfo; // 스프링의 PageVO 객체
            let paginationHtml = '';

            // [이전] 버튼 (prev가 true일 때)
            if (pageInfo.prev) {
                paginationHtml += `<li><a href="/board/qna?page=${pageInfo.startPage - 1}&category=${selectedCategory}">←</a></li>`;
            }

            // [숫자 페이지 번호] 반복 생성
            for (let i = pageInfo.startPage; i <= pageInfo.endPage; i++) {
                // 현재 머물고 있는 페이지면 active 클래스 추가
                const activeClass = (pageInfo.page === i) ? 'active' : '';
                paginationHtml += `<li>
                    <a href="/board/qna?page=${i}&category=${selectedCategory}" class="${activeClass}">${i}</a>
                </li>`;
            }

            // [다음] 버튼 (next가 true일 때)
            if (pageInfo.next) {
                paginationHtml += `<li><a href="/board/qna?page=${pageInfo.endPage + 1}&category=${selectedCategory}">→</a></li>`;
            }

            paginationContainer.innerHTML = paginationHtml;
        })
        .catch(error => console.error('Error:', error));
});