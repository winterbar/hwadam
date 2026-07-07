// 등록버튼 변수 생성
const commentInsertBtn = document.getElementById('comment-submit');

const boardId = Number(window.location.pathname.split("/").pop());

document.addEventListener("DOMContentLoaded", function(){

    getReplyList();

});

commentInsertBtn.addEventListener('click', ()=>{

    
    // 내용을 가져온다
    const content = document.getElementById('comment-content').value.trim();

    // 내용을 가져오긴 했는데... 뭐해야해?

    if (!content) {
        alert("댓글을 입력하세요.");
        return;
    }
    
    insertReply(content);

})

function insertReply(content){

    

    // 난 아직도 js를 모르겠어
    // 모르겠다고.......
    // 이 콘텐츠를 포스트 해야하나?
    fetch(`/api/board/reply`, {
        
        method: "POST",

        headers: {
        "Content-Type": "application/json"
        },  
        
        body: JSON.stringify({

            boardId: boardId,
            replyContent: content

        })

    })
    .then(function(response) {

        if (response.ok) {

            console.log(response);

            // 댓글 다시 조회
            getReplyList();

            // textarea 비우기
            document.getElementById("comment-content").value = "";


        }

    }).catch(function(error){
        console.error(error);
    });

}

function getReplyList(){
    fetch(`/api/board/reply/${boardId}`,

        {method: "GET"}
    ).then(res=>res.json())
    .then(data=>{

        drawReplyList(data.reList);
        document.getElementById("replyCount").textContent = data.rCount;

    });
}

function drawReplyList(replyList){

    let html = "";

    replyList.forEach(function(reply) {

        html += createReplyHtml(reply);

    });

    document.querySelector(".comment-list").innerHTML = html;
}

function createReplyHtml(reply){
    const html = `<div class="comment-item">
                    <div class="comment-header">
                        <div class="comment-info">
                            <span class="comment-author">${reply.nickname}</span>
                            <span class="comment-date">2026-06-18</span>
                        </div>

                        <div class="comment-actions">
                            <button type="button" class="btn-comment-edit">수정</button>
                            <button type="button" class="btn-comment-delete">삭제</button>
                        </div>
                    </div>
                    <div class="comment-content">${reply.replyContent}</div>
                </div>`

    return html;
}