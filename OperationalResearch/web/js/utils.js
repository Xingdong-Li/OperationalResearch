
function getParameter(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = location.search.substr(1).match(reg);
    if (r!=null) return (r[2]); return null;
}

function generateMessage(id, message, appendWhere) {
    $('#' + id).remove();
    let text = '<div class="alert alert-warning alert-dismissible" role="alert" style="text-align: left;" id="' + id + '">\n' +
        '                            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>\n' +
        '                            <strong>' + message + '</strong>\n' +
        '                        </div>\n';
    $('#' + appendWhere).append(text);
}

function isNotNegaNumber(val) {
    var regPos = /^\d+$/; //非负整数
    return regPos.test(val);
}

function isNumber(val) {
    var regPos = /^-\d+$/;
    return  isNotNegaNumber(val) || regPos.test(val);
}

function isFloat(val) {
    var regPos = /^-?[0-9]+\.?[0-9]+?$/;
    return  regPos.test(val);
}

function isNotZeroNumber(val) {
    return isNumber(val) && val!=0;
}

