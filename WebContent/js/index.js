var backups = [];
var $mainTab = $("a[href='#CCadaLogger']");
var $aboutTab = $("a[href='#AboutUs']");
var activeTab = "C-Cada Logger";
var previousTab;

$(document)
.ready(function() {
  $(".panel").hide();
  GetLoggers();
  BuildTable();
})
.ajaxStart(function(){
    $("div#Loading").addClass('show');
})
.ajaxStop(function(){
    $("div#Loading").removeClass('show');
});

$('#BackupList').on('shown.bs.modal', function() {
    $(this).find('.modal-body').empty();
    var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
    list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
    list += '<div id="searchlist" class="list-group">';

    $.each(backups, function(index, _id){
      list += '<a href="#" class="list-group-item" onClick="OpenBackup(' + index + '); return false;"><span>' + _id + '</span></a>';
    });
    list += '</div></form><script>$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false,});</script>';
    $(this).find('.modal-body').append(list);

});

$('#BackupName').on('shown.bs.modal', function() {
    $(this).find('.modal-body').empty();
    var html = [
      '<div class="container-fluid"><div class="row"><div class="form-group"><div class="input-group">',
	    '<span class="input-group-addon">backup-</span>',
      '<input type="text" id="BackupNumber" class="form-control">',
      '</div></div></div></div>',
    ].join('');

    $(this).find('.modal-body').append(html);
    $(this).find('#BackupNumber').focus().val("NNN");

});

$mainTab.on('shown.bs.tab', function(e) {
  $(".panel").hide();
  $("#Toolbar").show();
  $("#LoggerDatas").show();
});

$aboutTab.on('shown.bs.tab', function(e) {
  $(".panel").show();
  $("#Toolbar").hide();
  $("#LoggerDatas").hide();
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
  // cols.push({field:"index", title: "Index", align:"center", formatter: "IndexFormatter", sortable: false});
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
        ExpandTable($detail, row.positions, row);
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

  if (selectedLogger == 'Choose a table...' || selectedLogger == '') {
		ShowAlert("GetLoggerDatas()", "No logger selected.", "alert-warning");
		return;
	}

  var logger = {tid: "", uid: "", flag: 0};
  logger.tid = selectedLogger.split(' - ')[0];
  logger.uid = selectedLogger.split(' - ')[1];
  logger.flag = selectedLogger.split(' - ')[2];

  var $table = $('#LoggerDatas');

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "getpositions", "logger": {"flag": ' + logger.flag + ', "uid":"' + logger.uid + '", "tid": "' + logger.tid + '" }}',

    success: function(data) {
      console.log(data);
			if (data.RESPONSE.positions.length == 0) {
				showAlert("GetLoggerDatas()", "No data returned from logger.", "alert-info");
			}

			$table.bootstrapTable('append', data.RESPONSE);
      $("span.glyphicons-server").removeClass('testko');
      $("span.glyphicons-server").addClass('testok');
    },
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
      $("span.glyphicons-server").removeClass('testok');
      $("span.glyphicons-server").addClass('testko');
    }

  });
}

function OpenBackup(index){

  $('#BackupList').modal('toggle');
  var $table = $('#LoggerDatas');

  var _id = backups[index];

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "GetDBDoc", "_id": "' + _id + '"}',

    success: function(data) {
      console.log(data);
      if(data.RESPONSE.STATUS == "OK"){
        $table.bootstrapTable('load', data.RESPONSE.RESULT.loggers);
        ShowAlert("OpenBackup()", data.RESPONSE.MESSAGE, "alert-success");
        $("span.glyphicons-database").removeClass('testko');
        $("span.glyphicons-database").addClass('testok');

      }
      else{
        var errMsg = 'MESSAGE: ' + data.RESPONSE.MESSAGE + '<br>ERROR: ' + data.RESPONSE.ERROR + '<br>REASON: ' +
          data.RESPONSE.REASON + '<br>TROUBLESHOOTING: ' + data.RESPONSE.TROUBLESHOOTING;
        ShowAlert("OpenBackup()", errMsg, "alert-danger");
        $("span.glyphicons-database").removeClass('testok');
        $("span.glyphicons-database").addClass('testko');

      }
    },
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });

}

function RestoreFromDB(){

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "GetDBDocs"}',

    success: function(data) {
      console.log(data);
      if(data.RESPONSE.STATUS == "OK"){
        backups = data.RESPONSE.RESULT;
        if(backups.length > 0){
          // var resp = confirm("Current datas will be erased. Continue ?");
          var $table = $('#LoggerDatas');
          console.log("$table.bootstrapTable('getData')");
          console.log($table.bootstrapTable('getData').length);
          if($table.bootstrapTable('getData').length > 0){
            bootbox.confirm({
              message: "Current datas will be erased. Continue ?",
              buttons: {
                confirm: {
                    label: '<span class="glyphicon glyphicon-ok aria-hidden="true">',
                    className: 'btn btn-primary'
                },
                cancel: {
                    label: '<span class="glyphicon glyphicon-remove aria-hidden="true">',
                    className: 'btn btn-default'
                }
              },
              callback: function(result){
                if(result){
                  $('#BackupList').modal('toggle');
                }
              }
            });
          }
          else{
            $('#BackupList').modal('toggle');
          }

          $("span.glyphicons-database").removeClass('testko');
          $("span.glyphicons-database").addClass('testok');

        }
        else{
          ShowAlert("RestoreFromDB()", "No backup available yet..", "alert-info")
        }
      }
      else{
        var errMsg = 'MESSAGE: ' + data.RESPONSE.MESSAGE + '<br>ERROR: ' + data.RESPONSE.ERROR + '<br>REASON: ' +
          data.RESPONSE.REASON + '<br>TROUBLESHOOTING: ' + data.RESPONSE.TROUBLESHOOTING;
        ShowAlert("RestoreFromDB()", errMsg, "alert-danger");
        $("span.glyphicons-database").removeClass('testok');
        $("span.glyphicons-database").addClass('testko');
      }
    },
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });
}

function GetBackupName(){

  var $table = $('#LoggerDatas');
  var data = JSON.stringify($table.bootstrapTable('getData'));
  if(!data || 0 === data.length || data == '[]'){
    ShowAlert("SaveToDB()", "Nothing to save.", "alert-warning");
		return;
  }

  $("#BackupName").modal('toggle');

}

function SaveToDB(){

  var backupNumber = $("#BackupName").find('#BackupNumber').val();
  if (!$.isNumeric(backupNumber)) {
      ShowAlert("SaveToDB()", "Enter a numeric value.", "alert-warning");
      return;
  }

  var $table = $('#LoggerDatas');
  var data = JSON.stringify($table.bootstrapTable('getData'));
  var fd = new FormData();
  var file = new Blob([data], {type: 'plain/text'});

  fd.append('backup', file);
  fd.append('parms', '{"action": "savetoDb", "backupName": "backup-' + backupNumber + '"}')

  $.ajax({
    url: "CCadaLogger",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    dataType: 'json',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType

    success: function(data) {
      console.log(data);
      if(data.RESPONSE.STATUS == "OK"){
			  ShowAlert("SaveToDB()", data.RESPONSE.MESSAGE, "alert-success");
        $("span.glyphicons-database").removeClass('testko');
        $("span.glyphicons-database").addClass('testok');

      }
      else{
        var errMsg = 'MESSAGE: ' + data.RESPONSE.MESSAGE + '<br>ERROR: ' + data.RESPONSE.ERROR + '<br>REASON: ' +
          data.RESPONSE.REASON + '<br>TROUBLESHOOTING: ' + data.RESPONSE.TROUBLESHOOTING;
        ShowAlert("SaveToDB()", errMsg, "alert-danger");
        $("span.glyphicons-database").removeClass('testok');
        $("span.glyphicons-database").addClass('testko');

      }
		},
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
    }
  });

  $("#BackupName").modal('toggle');

}

function RemoveAll(){
  $('#LoggerDatas').bootstrapTable("removeAll");
}


function GetLoggers(){

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "GetLoggers"}',

    success: function(data) {
      $selectLogger = $('#SelectLogger');

      $selectLogger.empty();

      console.log(data);

      $.each(data.RESPONSE, function(index, logger){
        var value = logger.tid + ' - ' + logger.uid + ' - ' + logger.flag;
        console.log(value);

        var option = '<option value="' + value + '" data-subtext="' + logger.tid + ' - ' + logger.flag + '">' + logger.uid + '</option>';
        $selectLogger.append(option);
      });

      $selectLogger.selectpicker('refresh');
    },
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });

}

function TestServerConnection(){

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "tstsrvconn"}',

    success: function(data) {
      console.log(data);
      if(data.RESPONSE == "OK"){
        ShowAlert("TestServerConnection()", "Connection to UDP server was successfull.", "alert-success");
        $("span.glyphicons-server").removeClass('testko');
        $("span.glyphicons-server").addClass('testok');
      }
      else{
        ShowAlert("TestServerConnection()", "Connection to UDP server failed.", "alert-danger");
        $("span.glyphicons-server").removeClass('testok');
        $("span.glyphicons-server").addClass('testko');
      }
    },
    error: function(request, status, error) {
      $("span.glyphicons-server").removeClass('testok');
      $("span.glyphicons-server").addClass('testko');
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });

}


function TestDBConnection(){

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "tstdbconn"}',

    success: function(data) {
      console.log(data);
      if(data.RESPONSE == "OK"){
        ShowAlert("TestDBConnection()", "Connection to database was successfull.", "alert-success");
        $("span.glyphicons-database").removeClass('testko');
        $("span.glyphicons-database").addClass('testok');
      }
      else{
        ShowAlert("TestDBConnection()", "Connection to database failed.", "alert-danger");
        $("span.glyphicons-database").removeClass('testok');
        $("span.glyphicons-database").addClass('testko');
      }
    },
    error: function(request, status, error) {
      $("span.glyphicons-database").removeClass('testok');
      $("span.glyphicons-database").addClass('testko');
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });

}

function Reset(){

  $.ajax({
    type: 'POST',
    url: "CCadaLogger",
    dataType: 'json',
    data: '{"action": "reset"}',

    success: function(data) {
      console.log(data);
      // ShowAlert("Reset()", "Connection to database was successfull.", "alert-success");
    },
    error: function(request, status, error) {
      console.log(request);
      console.log(status);
      console.log(error);
    }

  });

  location.reload(true);

}

function ShowAlert(title, message, alertType, area) {

    $('#alertmsg').remove();

    var timeout = 5000;

    if(area == undefined){
      area = "bottom";
    }
    if(alertType.match('warning')){
      area = "bottom";
      timeout = 10000;
    }
    if(alertType.match('danger')){
      area = "bottom";
      timeout = 30000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<h4>' + title + '</h4>' +
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType + ' flyover flyover-' + area);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<h4>' + title + '</h4>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible flyover flyover-' + area);
    }

    $('#Alert').append($newDiv);

    if ( !$('#alertmsg').is( '.in' ) ) {
      $('#alertmsg').addClass('in');

      setTimeout(function() {
         $('#alertmsg').removeClass('in');
      }, timeout);
    }
}
