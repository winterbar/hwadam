// 등록버튼 변수 생성
const commentInsertBtn = document.getElementById('comment-submit');

const boardId = Number(window.location.pathname.split("/").pop());

// 수정 변수 선언
let editingReplyId = null;
// 대댓용 변수 선언
let replyingReplyId = null;

document.addEventListener("DOMContentLoaded", function () {

    getReplyList();

});

// 등록버튼 클릭 
commentInsertBtn.addEventListener('click', () => {


    // 내용을 가져온다
    const content = document.getElementById('comment-content').value.trim();

    // 내용을 가져오긴 했는데... 뭐해야해?

    if (!content) {
        alert("댓글을 입력하세요.");
        return;
    }

    insertReply(content);

})

// 수정/삭제버튼 클릭
const commentList = document.querySelector(".comment-list");

commentList.addEventListener("click", function (e) {

    console.log(e.target);

    if (e.target.classList.contains("btn-comment-edit")) {

        editingReplyId = Number(e.target.dataset.replyId);
        replyingReplyId = null;

        getReplyList();

    }
    if (e.target.classList.contains("btn-comment-delete")) {

        const replyId = Number(e.target.dataset.replyId);

        console.log(replyId);

        if (confirm("정말 삭제하시겠습니까?")) {
            deleteReply(replyId);
            alert("삭제되었습니다.");
        }

    }

    // 수정 활성화할 때 수정 / 취소
    if (e.target.classList.contains("btn-comment-update")) {

        const replyId = Number(e.target.dataset.replyId);

        const commentItem = e.target.closest(".comment-edit-item");

        const content = commentItem.querySelector(".comment-edit-content").value;

        console.log(replyId);

        console.log(content);

        updateReply(replyId, content);

        editingReplyId = null;


    }
    if (e.target.classList.contains("btn-comment-cancel")) {
        editingReplyId = null;


        replyingReplyId = null;

        getReplyList();
    }

    if (e.target.classList.contains("btn-comment-reply")){

        replyingReplyId = Number(e.target.dataset.replyId);

        editingReplyId = null;

        getReplyList();

    }

    // 답글

    if (e.target.classList.contains("btn-comment-reply-submit")) {
        const parentsReplyId = Number(e.target.dataset.replyId);

        const replyInput = e.target
            .closest(".child-reply-input")
            .querySelector(".child-reply-content");

        const content = replyInput.value.trim();

        if (!content) {
            alert("댓글을 입력하세요.");
            return;
        }

        insertReply(content, parentsReplyId)
    }

});

// 댓글 삽입
function insertReply(content, parentsReplyId = null) {

    fetch(`/api/board/reply`, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({

            boardId: boardId,
            parentsReplyId: parentsReplyId,
            replyContent: content

        })

    })
        .then(function (response) {

            if (response.ok) {

                console.log(response);

                // 댓글 다시 조회
                getReplyList();

                // textarea 비우기
                document.getElementById("comment-content").value = "";


            }

        }).catch(function (error) {
            console.error(error);
        });

}

// 댓글 수정
function updateReply(replyId, content) {

    fetch("/api/board/reply", {
        method: "PUT",

        headers: {

            "Content-Type": "application/json"

        },

        body: JSON.stringify({

            replyId: replyId,
            replyContent: content

        })
    })
        .then(function (response) {

            if (response.ok) {

                console.log(response);

                // 댓글 다시 조회
                getReplyList();


            }

        }).catch(function (error) {
            console.error(error);
        });
}

// 댓글 삭제(사실 상태 변경), 댓글 아이디만 전송한다
function deleteReply(replyId) {

    fetch("/api/board/reply/delete", {
        method: "PUT",

        headers: {

            "Content-Type": "application/json"

        },

        body: JSON.stringify({

            replyId: replyId

        })
    }).then(function (response) {

        if (response.ok) {

            console.log(response);

            // 댓글 다시 조회
            getReplyList();


        }

    }).catch(function (error) {
        console.error(error);
    });

}

// 댓글 목록 가져오기
function getReplyList() {
    fetch(`/api/board/reply/${boardId}`,

        { method: "GET" }
    ).then(res => res.json())
        .then(data => {

            drawReplyList(data.reList);
            document.getElementById("replyCount").textContent = data.rCount;

        });
}

// 댓글목록 그리기
function drawReplyList(replyList) {

    let html = "";

    const parentList =
        replyList.filter(reply => (reply.parentsReplyId === null && reply.deleted === false));

    parentList.forEach(parent => {

        if (parent.replyId === editingReplyId) {

            html += createReplyEditHtml(parent);

        } else {

            html += createReplyHtml(parent);

        }

        if (parent.replyId === replyingReplyId) {

            html += createReplyInputHtml(parent);

        }

        const childList =
            replyList.filter(
                reply => (reply.parentsReplyId === parent.replyId
                    && reply.deleted === false));

        childList.forEach(function (child) {

            if (child.replyId === editingReplyId) {

                html += createReplyEditHtml(child);

            } else {

                html += createReplyHtml(child);

            }

        });

    });

    document.querySelector(".comment-list").innerHTML = html;
}

// 댓글 html
// 대댓이냐 아니냐를 가른다. 
function createReplyHtml(reply) {
    const isChild = reply.parentsReplyId != null;

    const html = `
        <div class="comment-item ${isChild ? "child-comment" : ""}">

            <div class="comment-header">

                <div class="comment-info">

                    <span class="comment-author">
                        ${reply.nickname}
                    </span>

                    <span class="comment-date">
                        ${reply.createdAt}
                    </span>

                </div>

                <div class="comment-actions">

                    ${!isChild ? `
                        <button
                            type="button"
                            class="btn-comment-reply"
                            data-reply-id="${reply.replyId}">
                            답글
                        </button>
                    ` : ""}

                    <button
                        type="button"
                        class="btn-comment-edit"
                        data-reply-id="${reply.replyId}">
                        수정
                    </button>

                    <button
                        type="button"
                        class="btn-comment-delete"
                        data-reply-id="${reply.replyId}">
                        삭제
                    </button>

                </div>

            </div>

            <div class="comment-content">

                ${isChild ? `
                    <span class="reply-mention">
                        부모댓글닉네임아직없어요어어
                    </span>
                ` : ""}

                ${reply.replyContent}

            </div>

        </div>
    `;

    return html;
}

function createReplyEditHtml(reply) {
    const isChild = reply.parentsReplyId != null;

    const html = `
        <div class="comment-edit-item ${isChild ? "child-comment" : ""}">

            <div class="comment-header">

                <div class="comment-info">

                    <span class="comment-author">
                        ${reply.nickname}
                    </span>

                    <span class="comment-date">
                        ${reply.createdAt}
                    </span>

                </div>

            </div>

            <div class="comment-edit-area">

                ${isChild ? `
                    <div class="reply-mention">
                        @${reply.parentNickname}
                    </div>
                ` : ""}

                <textarea
                    class="comment-edit-content"
                    rows="2">${reply.replyContent}</textarea>

                <div class="comment-btn-edit-group">

                    <button
                        type="button"
                        class="btn btn-comment-cancel">
                        취소
                    </button>

                    <button
                        type="button"
                        class="btn btn-comment-update"
                        data-reply-id="${reply.replyId}">
                        수정
                    </button>

                </div>

            </div>

        </div>
    `;

    return html;
}

function createReplyInputHtml(parent) {

    const html = `
        <div class="child-reply-input">

            <div class="reply-target">

                <span class="reply-arrow">↳</span>

                <span class="reply-mention">
                    @${parent.nickname}
                </span>

            </div>

            <textarea
                class="child-reply-content"
                rows="2"
                placeholder="답글을 입력하세요."></textarea>

            <div class="comment-btn-group">

                <button
                    type="button"
                    class="btn btn-comment-cancel">
                    취소
                </button>

                <button
                    type="button"
                    class="btn btn-comment-reply-submit"
                    data-reply-id="${parent.replyId}">
                    등록
                </button>

            </div>

        </div>
    `;

    return html;
}