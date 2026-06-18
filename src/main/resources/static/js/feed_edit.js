document.addEventListener('DOMContentLoaded', function () {
    const tagInput = document.querySelector('.tag-row input');
    const tagAddBtn = document.querySelector('.tag-add-btn');
    const tagList = document.querySelector('.tag-list');
    const tagCount = document.querySelector('.tag-count');
    const imageItems = document.querySelectorAll('.current-image');
    const imageCount = document.querySelector('.image-current-count');
    const cancelBtn = document.querySelector('.cancel');
    const backBtn = document.querySelector('.back-link');

    function updateTagCount() {
        if (!tagCount || !tagList) return;
        const count = tagList.querySelectorAll('.tag-chip').length;
        tagCount.textContent = count + ' / 5';
    }

    function addTag() {
        if (!tagInput || !tagList) return;

        let value = tagInput.value.trim();
        if (!value) return;

        value = value.replace(/^#/, '');

        const currentTags = Array.from(tagList.querySelectorAll('.tag-chip')).map(function (chip) {
            return chip.dataset.tag;
        });

        if (currentTags.length >= 5) {
            alert('해시태그는 최대 5개까지 입력할 수 있어요.');
            return;
        }

        if (currentTags.includes(value)) {
            alert('이미 등록된 해시태그예요.');
            return;
        }

        const chip = document.createElement('span');
        chip.className = 'tag-chip';
        chip.dataset.tag = value;
        chip.innerHTML = '#' + value + ' <button type="button">×</button>';
        tagList.appendChild(chip);

        tagInput.value = '';
        updateTagCount();
    }

    if (tagAddBtn) {
        tagAddBtn.addEventListener('click', addTag);
    }

    if (tagInput) {
        tagInput.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                addTag();
            }
        });
    }

    if (tagList) {
        tagList.addEventListener('click', function (event) {
            if (event.target.tagName !== 'BUTTON') return;
            event.target.closest('.tag-chip').remove();
            updateTagCount();
        });
    }

    imageItems.forEach(function (item) {
        const deleteBtn = item.querySelector('button');
        if (!deleteBtn) return;

        deleteBtn.addEventListener('click', function () {
            item.remove();
            const remain = document.querySelectorAll('.current-image').length;
            if (imageCount) imageCount.textContent = remain + ' / 5';
        });
    });

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            history.back();
        });
    }

    if (backBtn) {
        backBtn.addEventListener('click', function () {
            history.back();
        });
    }

    updateTagCount();
});