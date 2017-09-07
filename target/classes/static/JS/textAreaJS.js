/**
 * Created by nitingoel on 5/25/16.
 */

//var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
//    //                                    mode: "application/xml",
//    styleActiveLine: true,
//    lineNumbers: true,
//    lineWrapping: true,
//    highlightSelectionMatches: {showToken: /\w/, annotateScrollbar: true}
//});


var editor = ace.edit("editor");
editor.setTheme("ace/theme/tomorrow");
editor.getSession().setMode("ace/mode/plp");