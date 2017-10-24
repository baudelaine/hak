$(document)
.ready(function() {
  SelectLogger();
  BuildTable();
})
.ajaxStart(function(){
    $("div#Loading").addClass('show');
})
.ajaxStop(function(){
    $("div#Loading").removeClass('show');
});

function SelectLogger(){

  $.getJSON("res/request.json", function(data){

    $selectLogger = $('#SelectLogger');

    $selectLogger.empty();

    $.each(data, function(index, logger){
      var value = logger.tid + ' - ' + logger.uid + ' - ' + logger.flag;
      console.log(value);

      var option = '<option value="' + value + '" data-subtext="' + logger.tid + ' - ' + logger.flag + '">' + logger.uid + '</option>';
      $selectLogger.append(option);
    });

    $selectLogger.selectpicker('refresh');
  })
    .done(function( json ) {
      ShowAlert("SelectLogger()", "Loggers list loaded successfully.", "alert-success");
    })
    .fail(function( jqxhr, textStatus, error ) {
      var err = textStatus + ", " + error;
      ShowAlert("SelectLogger()", "Error: " + err + " when loading loggers list.", "alert-danger");
    });
}

function BuildTable() {

  $table = $('#LoggerDatas');

  var cols = [];
  // cols.push({field:"checkbox", checkbox: "true"});
  cols.push({field:"index", title: "Index", align:"center", formatter: "IndexFormatter", sortable: false});
  cols.push({field:"uid", title: "UID", align:"center", sortable: true});
  cols.push({field:"tid", title: "TID", align:"center", sortable: true});
  cols.push({field:"flag", title: "Flag", align:"center", sortable: true});

  $table.bootstrapTable({
      columns: cols,
      // url: url,
      // data: data,
      search: false,
			showRefresh: false,
			showColumns: false,
			showToggle: false,
			pagination: false,
			showPaginationSwitch: false,
      idField: "index",
			// toolbar: "#DatasToolbar",
      detailView: true,
      onClickCell: function (field, value, row, $element){

      },
      onExpandRow: function (index, row, $detail) {
        var response;
        $.getJSON( "res/response.json", function(data){
          response = data;
          console.log(response.positions);
          ExpandTable($detail, response.positions, row);
        });
      }
  });
}

function IndexFormatter(value, row, index) {
  row.index = index;
  return index;
}

function ExpandTable($detail, data, parentData) {
    $subtable = $detail.html('<table></table>').find('table');
    console.log(data);
    BuildSubTable($subtable, data, parentData);
}

function latFormatter(value, row, index) {
  return row.lat;
}

function lngFormatter(value, row, index) {
  return row.lng;
}

function timestampFormatter(value, row, index) {
  return row.timestamp;
}

// function flagFormatter(value, row, index) {
//   return row.flag;
// }

function BuildSubTable($el, data, parentData){

  var cols = [];
  // cols.push({field:"checkbox", checkbox: "true"});
  var row0 = [];
  row0.push({field:"index", title: "Index", align:"center", valign:"middle", rowspan: 2, formatter: "IndexFormatter", sortable: false});
  // row0.push({field:"flag", title: "Flag", rowspan: 2, formatter: "flagFormatter", sortable: true});
  row0.push({field:"positions", title: "Positions", align:"center", colspan: 3, sortable: true});

  var row1 = [];
  row1.push({field:"lat", title: "Latitude", align:"center", formatter: "latFormatter", sortable: true});
  row1.push({field:"lng", title: "Longitude", align:"center", formatter: "lngFormatter", sortable: true});
  row1.push({field:"timestamp", title: "Timestamp", align:"center", formatter: "timestampFormatter", sortable: true});

  cols.push(row0);
  cols.push(row1);

  $el.bootstrapTable({
      columns: cols,
      // url: url,
      data: data,
      showToggle: false,
      search: false,
      checkboxHeader: false,
      idField: "index",
      onEditableInit: function(){
        //Fired when all columns was initialized by $().editable() method.
      },
      onEditableShown: function(editable, field, row, $el){
        //Fired when an editable cell is opened for edits.
      },
      onEditableHidden: function(field, row, $el, reason){
        //Fired when an editable cell is hidden / closed.
      },
      onEditableSave: function (field, row, oldValue, editable) {
        //Fired when an editable cell is saved.
        console.log("---------- buildSubTable: onEditableSave -------------");
        console.log("editable=");
        console.log(editable);
        console.log("field=");
        console.log(field);
        console.log("row=");
        console.log(row);
        console.log("oldValue=");
        console.log(oldValue);
        console.log("---------- buildSubTable: onEditableSave -------------");
      },
      onClickCell: function (field, value, row, $element){

      }
  });

}

function GetLoggerDatas(){

  $selectLogger = $('#SelectLogger');

  var selectedLogger = $selectLogger.find("option:selected").val();

  var logger = {tid: "", uid: "", flag: 0};
  logger.tid = selectedLogger.split(' - ')[0];
  logger.uid = selectedLogger.split(' - ')[1];
  logger.flag = selectedLogger.split(' - ')[2];

  $table = $('#LoggerDatas');

  $table.bootstrapTable("filterBy", {});
	nextIndex = $table.bootstrapTable("getData").length;
	console.log("nextIndex=" + nextIndex);
	$table.bootstrapTable('insertRow', {index: nextIndex, row: logger});

}

function ShowAlert(title, message, alerttype, area) {

    $('#alertmsg').remove();

    if(area == undefined){
      area = "bottom";
    }

    var $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<h4>' + title + '</h4>' +
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alerttype + ' flyover flyover-' + area);

    $('#Alert').append($newDiv);

    if ( !$('#alertmsg').is( '.in' ) ) {
      $('#alertmsg').addClass('in');

      setTimeout(function() {
         $('#alertmsg').removeClass('in');
      }, 3200);
    }
}
