document.addEventListener("DOMContentLoaded", function(){

    const buttonField = document.getElementById('action-field');
    const boardId = buttonField.dataset.id;

    fetch(`/api/board/${boardId}/check-id`, {method: 'GET'})
    .then(res =>res.json())
    .then(data =>{

        console.log(data.isOwner);
        console.log(typeof data.isOwner);

        if(data.isOwner === false){
            buttonField.style.display = "none";
        }

        console.log(buttonField.hidden);
        console.log(buttonField.outerHTML);
    })
});