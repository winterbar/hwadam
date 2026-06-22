document.addEventListener('DOMContentLoaded', function () {
    const filterBar = document.getElementById('feed-filter-bar');
    const filterSummary = document.getElementById('feed-filter-summary');
    const filterToggleIcon = document.getElementById('feed-filter-toggle-icon');
    const filterInner = document.getElementById('feed-filter-inner');
    const feedEmpty = document.getElementById('feed-empty');

    // CSRF 토큰 가져오기
    function getCsrfHeaders() {
        const csrfToken = document.querySelector('meta[name="_csrf"]');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

        const headers = {
            'Content-Type': 'application/json'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
        }

        return headers;
    }

    // 화면에 출력된 해시태그 필터 버튼 중 중복된 버튼을 제거하는 기능
    function removeDuplicateTagButtons() {
        const tagButtons = document.querySelectorAll('.feed-tag-btn');
        const seenTags = new Set();

        tagButtons.forEach(function (button) {
            const tag = button.dataset.tag
                ? button.dataset.tag.replace('#', '').trim().toLowerCase()
                : '';

            if (!tag) return;

            if (seenTags.has(tag)) {
                button.remove();
            } else {
                seenTags.add(tag);
            }
        });
    }

    // 피드 본문 + 정보공유 링크 출력 기능
    function renderFeedContentAndLink() {
        const feedCards = document.querySelectorAll('.feed-card');

        feedCards.forEach(function (card) {
            const rawContent = card.dataset.content
                ? card.dataset.content.trim()
                : '';

            const captionTextEl = card.querySelector('.js-caption-text');
            const link = card.querySelector('.feed-share-link');

            if (!captionTextEl || !link) return;

            link.style.display = 'none';

            if (!rawContent) return;

            let contentText = '';
            let linkText = '';

            function isLink(text) {
                if (!text) return false;

                const value = text.trim().toLowerCase();

                return value.startsWith('http://') || value.startsWith('https://');
            }

            if (rawContent.includes(',')) {
                const parts = rawContent.split(',');

                const first = parts[0] ? parts[0].trim() : '';
                const second = parts.length > 1 ? parts.slice(1).join(',').trim() : '';

                if (isLink(first)) {
                    linkText = first;
                } else {
                    contentText = first;
                }

                if (isLink(second)) {
                    linkText = second;
                }
            } else {
                if (isLink(rawContent)) {
                    linkText = rawContent;
                } else {
                    contentText = rawContent;
                }
            }

            if (contentText) {
                captionTextEl.textContent = ' ' + contentText;
            }

            if (linkText) {
                link.href = linkText;
                link.style.display = 'inline-flex';
            }
        });
    }

    // 피드 이미지 출력 기능
    function renderFeedImages() {
        const feedCards = document.querySelectorAll('.feed-card');

        feedCards.forEach(function (card) {
            const imageEl = card.querySelector('.js-feed-image');
            const placeholder = card.querySelector('.js-feed-placeholder');
            const badge = card.querySelector('.js-photo-badge');
            const prevBtn = card.querySelector('.feed-photo-prev');
            const nextBtn = card.querySelector('.feed-photo-next');

            const rawImages = card.dataset.images
                ? card.dataset.images.trim()
                : '';

            const images = rawImages
                .split(',')
                .map(function (name) {
                    return name.trim();
                })
                .filter(function (name) {
                    return name !== ''
                        && name !== 'null'
                        && name !== 'undefined';
                });

            let currentIndex = 0;

            function updateImage() {
                if (images.length === 0) {
                    if (imageEl) {
                        imageEl.removeAttribute('src');
                        imageEl.style.display = 'none';
                    }

                    if (placeholder) {
                        placeholder.style.display = 'flex';
                    }

                    if (badge) {
                        badge.style.display = 'none';
                    }

                    if (prevBtn) {
                        prevBtn.style.display = 'none';
                    }

                    if (nextBtn) {
                        nextBtn.style.display = 'none';
                    }

                    return;
                }

                if (imageEl) {
                    imageEl.src = '/upload/' + images[currentIndex];
                    imageEl.style.display = 'block';
                }

                if (placeholder) {
                    placeholder.style.display = 'none';
                }

                if (badge) {
                    badge.style.display = 'flex';
                    badge.textContent = (currentIndex + 1) + ' / ' + images.length;
                }

                if (prevBtn) {
                    prevBtn.style.display =
                        images.length > 1 && currentIndex > 0 ? 'flex' : 'none';
                }

                if (nextBtn) {
                    nextBtn.style.display =
                        images.length > 1 && currentIndex < images.length - 1 ? 'flex' : 'none';
                }
            }

            if (prevBtn) {
                prevBtn.addEventListener('click', function () {
                    if (images.length <= 1) return;
                    if (currentIndex <= 0) return;

                    currentIndex = currentIndex - 1;
                    updateImage();
                });
            }

            if (nextBtn) {
                nextBtn.addEventListener('click', function () {
                    if (images.length <= 1) return;
                    if (currentIndex >= images.length - 1) return;

                    currentIndex = currentIndex + 1;
                    updateImage();
                });
            }

            updateImage();
        });
    }

    // 해시태그 필터 열고 닫는 기능
    function toggleFeedFilter() {
        if (!filterInner) return;

        const isClosed = filterInner.style.display === 'none' || filterInner.style.display === '';

        if (isClosed) {
            filterInner.style.display = 'flex';

            if (filterBar) {
                filterBar.classList.remove('is-closed');
            }

            if (filterToggleIcon) {
                filterToggleIcon.innerText = '접기 －';
            }
        } else {
            filterInner.style.display = 'none';

            if (filterBar) {
                filterBar.classList.add('is-closed');
            }

            if (filterToggleIcon) {
                filterToggleIcon.innerText = '펼치기 ＋';
            }
        }
    }

    // 해시태그 필터 적용 기능
    function applyFilter(selectedTag) {
        const feedCards = document.querySelectorAll('.feed-card');
        let visibleCount = 0;

        feedCards.forEach(function (card) {
            const cardTags = card.dataset.tags || '';

            if (selectedTag === 'all' || cardTags.includes(selectedTag)) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        if (feedEmpty) {
            feedEmpty.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }

    // 좋아요 버튼 기능 - 서버 요청 + 하트 변경 + 카운트 변경
    function initLikeButtons() {
        document.querySelectorAll('.feed-like-btn').forEach(function (button) {
            const icon = button.querySelector('.feed-like-icon');
            const card = button.closest('.feed-card');
            const countText = card ? card.querySelector('.feed-like-count') : null;

            let isRequesting = false;

            let liked = button.dataset.liked === 'true';
            let count = Number(button.dataset.likeCount || 0);

            applyLikeUI(liked, count);

            button.addEventListener('click', function () {
                if (isRequesting) return;

                const feedId = card ? card.dataset.feedId : null;

                if (!feedId) {
                    alert('피드 번호를 찾을 수 없습니다.');
                    return;
                }

                isRequesting = true;

                fetch('/feed/' + feedId + '/like', {
                    method: 'POST',
                    headers: getCsrfHeaders()
                })
                    .then(function (response) {
                        if (response.status === 401 || response.status === 403) {
                            alert('로그인 후 좋아요를 누를 수 있습니다.');
                            throw new Error('login required');
                        }

                        if (!response.ok) {
                            alert('좋아요 처리 중 오류가 발생했습니다.');
                            throw new Error('like request failed');
                        }

                        return response.json();
                    })
                    .then(function (data) {
                        const serverLiked = data.liked === true || data.liked === 'true';
                        const serverLikeCount = Number(data.likeCount || 0);

                        applyLikeUI(serverLiked, serverLikeCount);
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
                    .finally(function () {
                        isRequesting = false;
                    });
            });

            function applyLikeUI(isLiked, likeCount) {
                button.dataset.liked = isLiked ? 'true' : 'false';
                button.dataset.likeCount = likeCount;

                if (isLiked) {
                    button.classList.add('is-liked');

                    if (icon) {
                        icon.textContent = '♥';
                        icon.style.color = '#111';
                    }
                } else {
                    button.classList.remove('is-liked');

                    if (icon) {
                        icon.textContent = '♡';
                        icon.style.color = '';
                    }
                }

                if (countText) {
                    countText.textContent = likeCount;
                }
            }
        });
    }

    // 댓글 영역 열고 닫기 기능
    function initCommentArea() {
        document.querySelectorAll('.feed-card').forEach(function (card) {
            const commentToggleBtn = card.querySelector('.feed-comment-toggle-btn');
            const commentBox = card.querySelector('.feed-comment-box');
            const commentListBtn = card.querySelector('.feed-comment-count');
            const commentList = card.querySelector('.feed-comment-list');

            const commentCount = Number(
                commentListBtn ? commentListBtn.dataset.commentCount || 0 : 0
            );

            if (commentListBtn) {
                commentListBtn.textContent = '댓글 ' + commentCount + '개 모두 보기';
            }

            if (commentBox) {
                commentBox.style.display = 'none';
            }

            if (commentList) {
                commentList.style.display = 'none';
            }

            if (commentToggleBtn && commentBox) {
                commentToggleBtn.addEventListener('click', function () {
                    const isOpen = commentBox.style.display === 'flex';
                    commentBox.style.display = isOpen ? 'none' : 'flex';
                });
            }

            if (commentListBtn && commentList) {
                commentListBtn.addEventListener('click', function () {
                    const isOpen = commentList.style.display === 'block';

                    commentList.style.display = isOpen ? 'none' : 'block';
                    commentListBtn.textContent = isOpen
                        ? '댓글 ' + commentCount + '개 모두 보기'
                        : '댓글 접기';
                });
            }
        });
    }

    // 피드 안에 해시태그 버튼 클릭 기능
    function initHashtagButtons() {
        document.querySelectorAll('.feed-hashtag-btn').forEach(function (button) {
            button.addEventListener('click', function () {
                const selectedTag = button.dataset.tag;

                document.querySelectorAll('.feed-tag-btn').forEach(function (tagBtn) {
                    tagBtn.classList.remove('active');

                    if (tagBtn.dataset.tag === selectedTag) {
                        tagBtn.classList.add('active');
                    }
                });

                applyFilter(selectedTag);

                if (filterInner) {
                    filterInner.style.display = 'flex';
                }

                if (filterBar) {
                    filterBar.classList.remove('is-closed');
                }

                if (filterToggleIcon) {
                    filterToggleIcon.innerText = '접기 －';
                }
            });
        });
    }

    // 로그인한 사용자와 게시글 작성자가 같은지 확인
    function isOwner(card) {
        if (!card) return false;

        const loginUsername = document.body.dataset.loginUsername
            ? document.body.dataset.loginUsername.trim()
            : '';

        const feedUsername = card.dataset.username
            ? card.dataset.username.trim()
            : '';

        if (!loginUsername || !feedUsername) {
            return false;
        }

        return loginUsername === feedUsername;
    }

    // 본인 게시글이 아니면 ... 메뉴 자체 숨기기
    function initOwnerMoreMenus() {
        document.querySelectorAll('.feed-card').forEach(function (card) {
            const moreWrap = card.querySelector('.feed-more-wrap');

            if (!moreWrap) return;

            if (isOwner(card)) {
                moreWrap.style.display = 'block';
            } else {
                moreWrap.style.display = 'none';
            }
        });
    }

    // 점 3개 피드 메뉴 기능
    function initMoreMenu() {
        document.querySelectorAll('.feed-more-btn').forEach(function (button) {
            button.addEventListener('click', function (event) {
                event.stopPropagation();

                const card = button.closest('.feed-card');

                if (!isOwner(card)) {
                    return;
                }

                const currentMenu = button
                    .closest('.feed-more-wrap')
                    .querySelector('.feed-more-menu');

                document.querySelectorAll('.feed-more-menu').forEach(function (menu) {
                    if (menu !== currentMenu) {
                        menu.classList.remove('is-open');
                    }
                });

                currentMenu.classList.toggle('is-open');
            });
        });

        document.addEventListener('click', function () {
            document.querySelectorAll('.feed-more-menu').forEach(function (menu) {
                menu.classList.remove('is-open');
            });
        });

        document.querySelectorAll('.feed-menu-item').forEach(function (btn) {
            btn.addEventListener('click', function (event) {
                event.stopPropagation();

                const card = btn.closest('.feed-card');

                if (!isOwner(card)) {
                    alert('본인이 작성한 피드만 수정하거나 삭제할 수 있습니다.');
                    return;
                }

                const feedId = card ? card.dataset.feedId : null;

                if (!feedId) {
                    alert('피드 번호를 찾을 수 없습니다.');
                    return;
                }

                // 삭제하기 버튼
                if (btn.classList.contains('danger')) {
                    const isDelete = confirm('해당 피드를 삭제하시겠습니까?');

                    if (!isDelete) {
                        return;
                    }

                    location.href = '/feed/' + feedId + '/delete';
                    return;
                }

                // 수정하기 버튼
                if (btn.classList.contains('edit-btn')) {
                    location.href = '/feed/' + feedId + '/edit';
                }
            });
        });
    }

    // 피드 페이지 처음 열릴 때 실행
    removeDuplicateTagButtons();
    renderFeedContentAndLink();
    renderFeedImages();
    initLikeButtons();
    initCommentArea();
    initHashtagButtons();
    initOwnerMoreMenus();
    initMoreMenu();

    // 상단 해시태그 필터 클릭 시
    if (filterSummary) {
        filterSummary.addEventListener('click', toggleFeedFilter);

        filterSummary.addEventListener('keydown', function (event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                toggleFeedFilter();
            }
        });
    }

    if (filterInner) {
        filterInner.addEventListener('click', function (event) {
            if (!event.target.classList.contains('feed-tag-btn')) return;

            document.querySelectorAll('.feed-tag-btn').forEach(function (button) {
                button.classList.remove('active');
            });

            event.target.classList.add('active');
            applyFilter(event.target.dataset.tag);
        });
    }
    
});
