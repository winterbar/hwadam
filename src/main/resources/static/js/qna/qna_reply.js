// 등록버튼 변수 생성
const commentInsertBtn = document.getElementById('comment-submit');

const boardId = Number(window.location.pathname.split("/").pop());

commentInsertBtn.addEventListener('click', ()=>{

    
    // 내용을 가져온다
    const content = document.getElementById('comment-content').value.trim();

    // 내용을 가져오긴 했는데... 뭐해야해?

    if (!content) {
        alert("댓글을 입력하세요.");
        return;
    }
    
    insertReply(content);

    getReplyList();

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


            // textarea 비우기

        }

    }).catch(function(error){
        console.error(error);
    });

}

function getReplyList(){
    fetch(`/api/board/reply/{boardId}`,

        {method: POST}
    ).then(function(response){
        return response.json();
    });
}