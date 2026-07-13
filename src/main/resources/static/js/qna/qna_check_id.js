document.addEventListener("DOMContentLoaded", function () {

    const buttonField = document.getElementById('action-field');
    const boardId = buttonField.dataset.id;

    fetch(`/api/board/${boardId}/check-id`, { method: 'GET' })
        .then(res => res.json())
        .then(data => {
            if (data.isOwner === false) {
                buttonField.style.display = "none";
            }
        })
});