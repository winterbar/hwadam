// 등록버튼 변수 생성
const commentInsertBtn = document.getElementById('comment-submit');
const commentArea = document.getElementById('comment-content');
const boardId = Number(window.location.pathname.split("/").pop());

// 수정 변수 선언
let editingReplyId = null;
// 대댓용 변수 선언
let replyingReplyId = null;
// 닉네임 저장하는 변수 선언 
let replyingNickname = null;

document.addEventListener("DOMContentLoaded", function () {

    getReplyList();

});

// 등록버튼 클릭 
commentInsertBtn.addEventListener('click', () => {

    if (commentArea.disabled === true){
        return;
    }


    // 내용을 가져온다
    const content = document.getElementById('comment-content').value.trim();

    if (!content) {
        alert("댓글을 입력하세요.");
        return;
    }

    insertReply(content);

})

// 수정/삭제버튼 클릭
const commentList = document.querySelector(".comment-list");

commentList.addEventListener("click", function (e) {

    if (e.target.classList.contains("btn-comment-edit")) {

        editingReplyId = Number(e.target.dataset.replyId);
        replyingReplyId = null;

        getReplyList();

    }
    if (e.target.classList.contains("btn-comment-delete")) {

        const replyId = Number(e.target.dataset.replyId);

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

        updateReply(replyId, content);

        editingReplyId = null;

        replyingReplyId = null;


    }
    if (e.target.classList.contains("btn-comment-cancel")) {
        editingReplyId = null;

        replyingReplyId = null;

        getReplyList();
    }

    // 답글 버튼 

    if (e.target.classList.contains("btn-comment-reply")) {

        replyingReplyId = Number(e.target.dataset.replyId);


        replyingNickname = e.target.dataset.nickname;

        editingReplyId = null;

        getReplyList();

    }

    // 답글 전송

    if (e.target.classList.contains("btn-comment-reply-submit")) {

        // const replyingReplyId = Number(e.target.dataset.replyId);

        const replyInput = e.target
            .closest(".child-reply-input")
            .querySelector(".child-reply-content");

        const content = replyInput.value.trim();

        if (!content) {
            alert("댓글을 입력하세요.");
            return;
        }

        insertReply(content, replyingReplyId);

        replyingReplyId = null;
    }

});

// 댓글 삽입
function insertReply(content, parentsReplyId = null) {

    // fetch - 객체 전달
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

    // fetch - 객체 전달
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

                // 댓글 다시 조회
                getReplyList();


            }

        }).catch(function (error) {
            console.error(error);
        });
}

// 댓글 삭제(사실 상태 변경), 댓글 아이디만 전송한다
function deleteReply(replyId) {

    // fetch - 객체 전달
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

            // 댓글 다시 조회
            getReplyList();


        }

    }).catch(function (error) {
        console.error(error);
    });

}

// 댓글 목록 가져오기
function getReplyList() {

    // fetch - 객체 받아옴 
    fetch(`/api/board/reply/${boardId}`,

        { method: "GET" }
    ).then(res => res.json())
        .then(data => {

            drawReplyList(data.reList, data.nowUse, data.role);
            document.getElementById("replyCount").textContent = data.rCount;

        });
}

// 댓글목록 그리기
function drawReplyList(replyList, logOn, role) {

    let html = "";

    const parentList =
        replyList.filter(reply => reply.parentsReplyId === null);

    parentList.forEach(parent => {

        if (parent.replyId === editingReplyId) {

            html += createReplyEditHtml(parent, replyList);

        } else {

            html += createReplyHtml(parent, replyList, logOn, role);

        }

        if (parent.replyId === replyingReplyId) {

            html += createReplyInputHtml();

        }

        html += drawChildReply(parent.replyId, replyList, logOn, role);

    });

    document.querySelector(".comment-list").innerHTML = html;
}

// 자식들을 모두 그린다.
function drawChildReply(parentId, replyList, logOn, role) {

    let html = "";

    const childList =
        replyList.filter(
            reply => reply.parentsReplyId === parentId);

    childList.forEach(function (child) {

        if (child.replyId === editingReplyId) {

            html += createReplyEditHtml(child, replyList);

        } else {

            html += createReplyHtml(child, replyList, logOn, role);

        }

        if (child.replyId === replyingReplyId) {

            html += createReplyInputHtml();

        }

        html += drawChildReply(child.replyId, replyList, logOn, role);

    });

    return html;

}

// 잠깐 대댓을 보유중인가?
function hasChildReply(reply, replyList) {

    return replyList.some(child =>
        child.parentsReplyId === reply.replyId &&
        !child.deleted
    );

}

// 댓글 html
// 대댓이냐 아니냐를 가른다. 
function createReplyHtml(reply, replyList, logOn, role) {

    if (reply.deleted) {

        if (!hasChildReply(reply, replyList)) {

            return "";

        }

        return `
        <div class="comment-item deleted-comment">

            <div class="comment-content">
                삭제된 댓글입니다.
            </div>

        </div>
    `;

    }

    const isChild = reply.parentsReplyId != null;

    const html = `
        <div class="comment-item ${isChild ? "child-comment" : ""}">

            <div class="comment-header">

                <div class="comment-info">

                    <span class="comment-author">
                        ${reply.nickname}
                    </span>

                </div>

                <div class="comment-actions">

                    <button
                        type="button"
                        class="btn-comment-reply"
                        data-reply-id="${reply.replyId}"
                        data-nickname="${reply.nickname}">
                        답글
                    </button>
                    ${logOn == reply.username ? `
                    <button
                        type="button"
                        class="btn-comment-edit"
                        data-reply-id="${reply.replyId}">
                        수정
                    </button>
                    ` : ""}
                    ${(logOn === reply.username || role === "ADMIN") ? `
                    <button
                        type="button"
                        class="btn-comment-delete"
                        data-reply-id="${reply.replyId}">
                        삭제
                    </button>
                    ` : ""}

                </div>

            </div>

            <div class="comment-content">

                ${isChild ? `
                    <span class="reply-mention">
                        @${reply.parentNickname}
                    </span>
                ` : ""}

                ${reply.replyContent}

            </div>

        </div>
    `;

    return html;
}

function createReplyEditHtml(reply, replyList) {
    const isChild = reply.parentsReplyId != null;

    const html = `
        <div class="comment-edit-item ${isChild ? "child-comment" : ""}">

            <div class="comment-header">

                <div class="comment-info">

                    <span class="comment-author">
                        ${reply.nickname}
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

function createReplyInputHtml() {

    const html = `
        <div class="child-reply-input">

            <div class="reply-target">

                <span class="reply-arrow">↳</span>

                <span class="reply-mention">
                    @${replyingNickname}
                </span>

            </div>

            <textarea
                class="child-reply-content"
                rows="2"
                placeholder="답글을 입력하세요."></textarea>

            <div class="comment-btn-group">

                <button
                    type="button"
                    class="btn-comment-cancel">
                    취소
                </button>

                <button
                    type="button"
                    class="btn-comment-reply-submit">
                    등록
                </button>

            </div>

        </div>
    `;

    return html;
}

