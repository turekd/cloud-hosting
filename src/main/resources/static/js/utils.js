function formatDatetime(timestamp) {
    var addPrefixZero = function (value) {
        if (value < 10) {
            value = '0' + value;
        }
        return value;
    };
    var date = new Date(timestamp);
    var day = addPrefixZero(date.getDate());
    var month = addPrefixZero(date.getMonth() + 1);
    var hours = addPrefixZero(date.getHours());
    var minutes = addPrefixZero(date.getMinutes());
    return day + '-' + (month) + '-' + date.getFullYear() + ' ' + hours + ':' + minutes;
}

function insertParam(key, value) {
    var url = new URL(window.location);
    url.searchParams.set(key, value === null ? '' : value);
    window.history.pushState("object or string", "Title", url);
}

// Mime types
var images = ["image/jpg", "image/jpeg", "image/png", "image/gif"];
var codes = ["application/x-php", "text/x-java", "text/x-python", "text/plain", "application/xml", "application/javascript",
    "application/x-shellscript", "text/html"];

function isImage(mimeType) {
    return images.indexOf(mimeType) !== -1;
}

function isCode(mimeType) {
    return config.readableMimeTypes.indexOf(mimeType) !== -1;
}

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

function escapeHtml(string) {
    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}

function allowNewlines(string) {
    return string.replace(/(?:\r\n|\r|\n)/g, '<br />');
}