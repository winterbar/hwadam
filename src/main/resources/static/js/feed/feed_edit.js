document.addEventListener('DOMContentLoaded', function () {
    renderEditFeedContentAndLink();
    initEditImageArea();
    initEditTagArea();
    initEditFormValidation();

    // 본문 + 정보공유 링크 분리해서 수정 input에 넣기
    function renderEditFeedContentAndLink() {
    const form = document.querySelector('form');
    if (!form) return;

    const contentInput = document.getElementById('editFeedContent');
    if (!contentInput) return;

    const rawContent = form.dataset.content
        ? form.dataset.content.trim()
        : '';

    if (!rawContent) return;

    contentInput.value = rawContent;
}

    // 이미지 추가/삭제 + 대표 이미지 미리보기
    function initEditImageArea() {
        const imageList = document.getElementById('current-image-list');
        const fileInput = document.getElementById('fileInput');
        const addBtn = document.getElementById('image-add-btn');
        const countEl = document.getElementById('image-current-count');
        const previewImage = document.getElementById('main-preview-image');
        const previewText = document.getElementById('main-preview-text');

        const MAX_IMAGE_COUNT = 5;

        if (!imageList || !fileInput || !addBtn) return;

        updateImageState();

        addBtn.addEventListener('click', function () {
            fileInput.click();
        });

        fileInput.addEventListener('change', function () {
            const selectedFiles = Array.from(fileInput.files);

            if (selectedFiles.length === 0) return;

            const currentCount = getImageBoxes().length;
            const availableCount = MAX_IMAGE_COUNT - currentCount;

            if (availableCount <= 0) {
                alert('이미지는 최대 5개까지 등록할 수 있습니다.');
                fileInput.value = '';
                return;
            }

            const filesToAdd = selectedFiles.slice(0, availableCount);

            if (selectedFiles.length > availableCount) {
                alert('이미지는 최대 5개까지 등록할 수 있어요. 가능한 개수만 추가됩니다.');
            }

            filesToAdd.forEach(function (file) {
                addNewImageBox(file);
            });

            updateImageState();
        });

        imageList.addEventListener('click', function (event) {
            const deleteBtn = event.target.closest('.current-image-delete-btn');

            if (!deleteBtn) return;

            const imageBox = deleteBtn.closest('.current-image');

            if (!imageBox) return;

            if (imageBox.dataset.imageType === 'existing') {
                const savedName = imageBox.dataset.savedName;

                if (savedName) {
                    addDeletedImageInput(savedName);
                }
            }

            imageBox.remove();
            updateImageState();
        });

        function getImageBoxes() {
            return Array.from(imageList.querySelectorAll('.current-image'));
        }

        function updateImageState() {
            const imageBoxes = getImageBoxes();
            const previewBox = document.getElementById('main-preview-box');

            imageBoxes.forEach(function (box) {
                box.classList.remove('is-main');
            });

            if (countEl) {
                countEl.textContent = imageBoxes.length + ' / ' + MAX_IMAGE_COUNT;
            }

            if (imageBoxes.length === 0) {
                if (previewBox) {
                    previewBox.classList.remove('has-image');
                    previewBox.classList.remove('preview-filled');
                }

                if (previewImage) {
                    previewImage.removeAttribute('src');
                    previewImage.style.display = 'none';
                }

                if (previewText) {
                    previewText.style.display = 'flex';
                    previewText.textContent = '대표 이미지 미리보기';
                }

                return;
            }

            if (previewBox) {
                previewBox.classList.add('has-image');
                previewBox.classList.add('preview-filled');
            }

            const mainBox = imageBoxes[0];
            mainBox.classList.add('is-main');

            const mainImg = mainBox.querySelector('img');

            if (mainImg && previewImage) {
                previewImage.src = mainImg.src;
                previewImage.style.display = 'block';
            }

            if (previewText) {
                previewText.style.display = 'none';
            }
        }

        function addNewImageBox(file) {
            const imageUrl = URL.createObjectURL(file);

            const imageBox = document.createElement('div');
            imageBox.className = 'current-image';
            imageBox.dataset.imageType = 'new';

            const img = document.createElement('img');
            img.src = imageUrl;
            img.alt = '새로 추가한 이미지';

            const deleteBtn = document.createElement('button');
            deleteBtn.type = 'button';
            deleteBtn.className = 'current-image-delete-btn';
            deleteBtn.textContent = '×';

            imageBox.appendChild(img);
            imageBox.appendChild(deleteBtn);

            imageList.appendChild(imageBox);
        }

        function addDeletedImageInput(savedName) {
            const form = document.querySelector('form');

            if (!form) return;

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'deletedImages';
            input.value = savedName;

            form.appendChild(input);
        }
    }

    // 태그 추가/삭제 기능
    function initEditTagArea() {
        const tagInput = document.getElementById('tagInput');
        const tagAddBtn = document.querySelector('.tag-add-btn');
        const tagList = document.getElementById('tag-list');
        const tagCount = document.querySelector('.tag-count');

        const MAX_TAG_COUNT = 5;

        if (!tagInput || !tagAddBtn || !tagList) return;

        normalizeExistingTagInputs();
        updateTagCount();

        tagAddBtn.addEventListener('click', function () {
            addTag();
        });

        tagInput.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                addTag();
            }
        });

        tagList.addEventListener('click', function (event) {
            const deleteBtn = event.target.closest('.tag-delete-btn');

            if (!deleteBtn) return;

            const tagChip = deleteBtn.closest('.tag-chip');

            if (!tagChip) return;

            tagChip.remove();
            updateTagCount();
        });

        function addTag() {
            let tagName = tagInput.value.trim();

            if (!tagName) {
                alert('해시태그를 입력해주세요.');
                return;
            }

            tagName = tagName.replace('#', '').trim();

            if (!tagName) {
                alert('해시태그를 입력해주세요.');
                return;
            }

            const currentTags = getCurrentTags();

            if (currentTags.length >= MAX_TAG_COUNT) {
                alert('해시태그는 최대 5개까지 입력할 수 있습니다.');
                return;
            }

            if (currentTags.includes(tagName)) {
                alert('이미 등록된 해시태그입니다.');
                tagInput.value = '';
                return;
            }

            const tagChip = document.createElement('span');
            tagChip.className = 'tag-chip';
            tagChip.dataset.tag = tagName;

            const tagText = document.createElement('span');
            tagText.textContent = '#' + tagName;

            const deleteBtn = document.createElement('button');
            deleteBtn.type = 'button';
            deleteBtn.className = 'tag-delete-btn';
            deleteBtn.dataset.tagName = tagName;
            deleteBtn.textContent = '×';

            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'tagNames';
            hiddenInput.value = tagName;

            tagChip.appendChild(tagText);
            tagChip.appendChild(deleteBtn);
            tagChip.appendChild(hiddenInput);

            tagList.appendChild(tagChip);

            tagInput.value = '';
            updateTagCount();
        }

        function getCurrentTags() {
            return Array.from(tagList.querySelectorAll('.tag-chip'))
                .map(function (chip) {
                    return chip.dataset.tag ? chip.dataset.tag.trim() : '';
                })
                .filter(function (tag) {
                    return tag !== '';
                });
        }

        function updateTagCount() {
            const count = tagList.querySelectorAll('.tag-chip').length;

            if (tagCount) {
                tagCount.textContent = count + ' / ' + MAX_TAG_COUNT;
            }
        }

        function normalizeExistingTagInputs() {
            const tagChips = tagList.querySelectorAll('.tag-chip');

            tagChips.forEach(function (chip) {
                const tagName = chip.dataset.tag ? chip.dataset.tag.trim() : '';
                const hiddenInput = chip.querySelector('input[type="hidden"]');

                if (hiddenInput) {
                    hiddenInput.name = 'tagNames';
                    hiddenInput.value = tagName;
                }
            });
        }
    }

    // 수정 완료 전 이미지/태그 개수 검사
    function initEditFormValidation() {
        const form = document.querySelector('form');
        const imageList = document.getElementById('current-image-list');
        const tagList = document.getElementById('tag-list');

        if (!form) return;

        form.addEventListener('submit', function (event) {
            const imageCount = imageList
                ? imageList.querySelectorAll('.current-image').length
                : 0;

            const tagCount = tagList
                ? tagList.querySelectorAll('.tag-chip').length
                : 0;

            if (imageCount < 1 || imageCount > 5) {
                event.preventDefault();
                alert('사진은 1개 이상 5개 이하로 등록해주세요.');
                return;
            }

            if (tagCount < 3 || tagCount > 5) {
                event.preventDefault();
                alert('해시태그는 3개 이상 5개 이하로 입력해주세요.');
                return;
            }
        });
    }
});