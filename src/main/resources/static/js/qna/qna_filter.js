
// 조회조건 객체

const searchCondition = {
    category: "",
    sort: "",
    keyword: "",
    searchType: "",
    startDate: "",
    endDate: "",
    page: 1
};

// 게시글 조회 함수
function loadBoardList(){
    searchCondition.category = document.getElementById("filter-status").value;
    searchCondition.sort = document.getElementById("filter-sort").value;
    searchCondition.startDate = document.getElementById("startDate").value;
    searchCondition.endDate = document.getElementById("endDate").value;
    searchCondition.keyword = document.getElementById("keyword").value;
    searchCondition.searchType = document.getElementById("searchType").value;

    const params = new URLSearchParams();

    params.append("category", searchCondition.category);
    params.append("sort", searchCondition.sort);

    if(searchCondition.keyword){
        params.append("keyword", searchCondition.keyword);
    }

    if(searchCondition.searchType){
        params.append("searchType", searchCondition.searchType);
    }

    if(searchCondition.startDate){
        params.append("startDate",
            searchCondition.startDate + "T00:00:00");
    }

    if(searchCondition.endDate){
        params.append("endDate",
            searchCondition.endDate + "T23:59:59");
    }

    params.append("page", searchCondition.page);

    console.log(`/api/board/filter?${params.toString()}`);

    // 스프링 API 호출
    fetch(`/api/board/filter?${params.toString()}`)
        .then(response => response.json())
        .then(data => {

            // 기존 목록을 비우고, 새로 받아온 데이터로 목록을 그려주는 메서드
            drawBoardList(data.list);
            // 새 목록에 맞게 페이징
            drawPagination(data.pageInfo);
        })
        .catch(error => console.error('Error:', error));

}

function drawBoardList(boardList){

    const listContainer =   
        document.getElementById('board-list-container')

    listContainer.innerHTML = "";

    if(!boardList || boardList.length===0){

        listContainer.innerHTML =
            `<tr><td colspan="6">게시글이 없습니다.</td></tr>`;

        return;
    }

    boardList.forEach(list => {

        // category = 

        const dateStr = list.createdAt ? list.createdAt.substring(0, 10).replace(/-/g, '.') : '';
        listContainer.innerHTML += `<tr>
            <td class="col-num">${list.boardId}</td>
            <td class="col-product" style="color: var(--color-accent); font-weight: 800;">[${list.category}]</td>
            <td>
                <a href="/community/${list.boardId}" class="col-title">${list.title}</a>
                <span class="reply-count">[${list.replyCnt}]</span>
            </td>
            <td class="col-writer">${list.nickname}</td>
            <td class="col-date">${dateStr}</td>
            <td class="col-hits">${list.viewCnt}</td>
            </tr>`;
    });

}

function drawPagination(pageInfo){

    const pagenation =
        document.getElementById('pagination-container');

        let paginationHtml =""

            if (!pagenation) return;
            pagenation.innerHTML = ''; // 기존 타임리프가 그린 페이지 번호 삭제

            // [이전] 버튼 (prev가 true일 때)
            if (pageInfo.prev) {
                paginationHtml += `<li>
                    <a href="#" data-page="${pageInfo.startPage - 1}">
                         ←
                    </a>
                </li>`;
            }

            // [숫자 페이지 번호] 반복 생성
            for (let i = pageInfo.startPage; i <= pageInfo.endPage; i++) {
                // 현재 머물고 있는 페이지면 active 클래스 추가
                const activeClass = (pageInfo.page === i) ? 'active' : '';
                paginationHtml += `
                    <li>
                        <a href="#" data-page="${i}"
                        class="${activeClass}">
                        ${i}
                        </a>
                    </li>`;
            }

            // [다음] 버튼 (next가 true일 때)
            if (pageInfo.next) {
                paginationHtml += `<li><a href="#" data-page="${pageInfo.endPage + 1}">→</a></li>`;
            }

            pagenation.innerHTML = paginationHtml;

            document.querySelectorAll("#pagination-container a")
            .forEach(a => {

                a.addEventListener("click", function(e){

                    e.preventDefault();

                    searchCondition.page = Number(this.dataset.page);

                    loadBoardList();

                });

            });

}

document.getElementById("filter-status")
.addEventListener("change", () => {

    searchCondition.page = 1;
    loadBoardList();

});

document.getElementById("filter-sort")
.addEventListener("change", () => {

    searchCondition.page = 1;
    loadBoardList();

});

document.getElementById("dateApply")
.addEventListener("click", () => {

    searchCondition.page = 1;
    loadBoardList();

});

document.getElementById("searchBtn").addEventListener("click", (e)=>{

    searchCondition.page = 1;

    loadBoardList();

});

// 최신순 or 조회순 

// 날짜 모달

const filterBtn = document.querySelector(".date-filter-btn");
const popup = document.querySelector(".date-popup");

filterBtn.addEventListener("click", function(e){
    e.stopPropagation();
    popup.classList.toggle("show");
});

document.addEventListener("click", function(e){
    if(!e.target.closest(".date-filter")){
        popup.classList.remove("show");
    }
});