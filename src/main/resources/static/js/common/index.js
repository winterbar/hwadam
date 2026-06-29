// 길이가 긴 제목 ... 처리
function truncateTitles(className, maxLength) {
    const titles = document.querySelectorAll(`.${className}`);
    titles.forEach(title => {
        const text = title.textContent;
        if (text.length > maxLength) {
        title.textContent = text.substring(0, maxLength) + '...';
        }
    });
}

// 댓글이 999개를 넘으면 999+로 출력
document.addEventListener('DOMContentLoaded', () => {
    const replyElements = document.querySelectorAll('.main-post-replies');
    
    replyElements.forEach(el => {
        const count = parseInt(el.textContent.trim());

        if (!isNaN(count)) {
            if (count > 999) {
                el.textContent = '[999+]';
            } else {
                el.textContent = `[${count}]`;
            }
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    truncateTitles('main-post-title', 33); // 30자 이상 제목은 ... 처리
});

function formatViewCount(num) {
    if (num >= 10000) {
        return (num / 10000).toFixed(1).replace('.0', '') + '만';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1).replace('.0', '') + '천';
    }
    return num + '회';
}

// 조회수 단위 설정
document.querySelectorAll('.main-post-meta').forEach(el => {
    const rawValue = parseInt(el.textContent);
    if (!isNaN(rawValue)) {
        el.textContent = formatViewCount(rawValue);
    }
});