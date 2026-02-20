"use strict";

// Class definition
var FundTransferFavoriteBulkInsert = function () {
  var tranType;
  var processUrl;

  var stepperEl;
  var stepperObj;

  var tableDefaultData;
  var ackTable;
  var ackTableContainer;
  var confirmationTable;
  var confirmationTableContainer;
  var detailsTable;
  var detailsTableContainer;

  var favoriteInputForm;
  var favoriteInputFormValidator;
  var fromAccNoSelect;
  var groupIdSelect;

  var initNextBtn;
  var submitBtn;
  var ackPrintBtn;

  var dataTableFields = [{
    title: "Beneficiary Account",
    className: "fw-bold fs-6 text-gray-800 min-win-175px text-center",
    width:200,
    data: function (data) {
        return '<span class="fw-semibold">' + data.toAccNo + '</span>';
    }
  },{
    title: "Beneficiary Name",
    className: "fw-bold fs-6 text-gray-800 min-w-175px text-center",
    width: 200,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.toAccName) + '</span>';
    }
  },{
    title: "Favourite Account Name",
    className: "fw-bold fs-6 text-gray-800 min-w-175px text-center",
    width:200,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.name) + '</span>';
    }
  }, {
    title: "Group Name",
    className: "fw-bold fs-6 text-gray-800 min-w-175px text-center",
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.groupName) + '</span>';
    }
  },{
    title: "Mobile No 1",
    className: "fw-bold fs-6 text-gray-800 min-w-125px text-center",
    width: 125,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.sms1) + '</span>';
    }
  }, {
    title: "Mobile No 2",
    className: "fw-bold fs-6 text-gray-800 min-w-125px text-center",
    width: 125,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.sms2) + '</span>';
    }
  },{
    title: "Email 1",
    className: "fw-bold fs-6 text-gray-800 min-w-125px text-center",
    width: 125,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.email1) + '</span>';
    }
  },{
    title: "Email 2",
    className: "fw-bold fs-6 text-gray-800 min-w-125px text-center",
    width: 125,
    data: function (data) {
      return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.email2) + '</span>';
    }
  }];

  const initStepper = function () {
    // Initialize Stepper
    stepperObj = new KTStepper(stepperEl, {startIndex: 1});

    initDetailsPage();
    stepperObj.on('kt.stepper.changed', function (stepper) {
      Array.from(stepperEl.querySelectorAll('button')).forEach(el => el.blur());
      window.scrollTo(0, 0);
      switch(stepper.getCurrentStepIndex()){
        case 2:
          initConfirmationPage();
          break;
      }
    });

    // Prev event
    stepperObj.on('kt.stepper.previous', function (stepper) {
      Array.from(stepperEl.querySelectorAll('button')).forEach(el => el.blur());
      stepper.goPrevious();
    });
  }

  const initFormEventListeners = function(){

    $(initNextBtn).on('click', function(e){
      e.preventDefault();
      e.stopPropagation();

      const fv = favoriteInputFormValidator;
      //fv.resetForm();
      fv.validate().then(function (status) {
        if (status == 'Valid') {
          PbeApp.showPageLoading();
          initNextBtn.disabled = true;
          initNextBtn.setAttribute('data-kt-indicator', 'on');

          var formData = {};
          $.map($(favoriteInputForm).serializeArray(), function( n, i ) {
            if(n.name != 'favoriteId' && n.name != '_csrf' && n.name != 'beneName'){
              formData[n.name] = n.value;
            }
          });

          favoriteVerifier(formData).then((data) => {
            PbeApp.hidePageLoading();
            initNextBtn.removeAttribute('data-kt-indicator');
            initNextBtn.disabled = false;

            if(data == null){
              window.location.replace("forceLogoutRedirect");
            }

            var title = null;
            var bodyMsg = null;

            if(data.hasOwnProperty('isExists') && data.isExists == true){
              title = 'Already Exist!';
              bodyMsg = 'Record already exists.';
            }else if(data.hasOwnProperty('isInvalid') && data.isInvalid == true){
              title = 'Invalid Account No';
              bodyMsg = 'Please insert a valid Account Number.';
            }else if(data.hasOwnProperty('isOwnAcc') && data.isOwnAcc == true){
              title = 'Own Account No!';
              bodyMsg = 'Please do not insert your own account number.';
            }

            if(title){
              Swal.fire({
                title: title,
                html: '<p class="pt-8 px-12 fw-semibold fs-6 text-danger">'+bodyMsg+'</p>',
                buttonsStyling: false,
                confirmButtonText: "Close",
                customClass: {
                  confirmButton: "btn btn-sm btn-secondary"
                }
              });
              return;
            }

            stepperObj.goNext();
          });
        }
      });
    });

    $(submitBtn).on('click', function(e){
      e.preventDefault();
      e.stopPropagation();

      PbeApp.showPageLoading();
      submitBtn.disabled = true;
      submitBtn.setAttribute('data-kt-indicator', 'on');

      const formData = detailsTable.data().toArray();
      ajaxSubmit(formData).then((data) => {
        PbeApp.hidePageLoading();
        submitBtn.removeAttribute('data-kt-indicator');
        submitBtn.disabled = false;
        
        stepperObj.goNext();

        initAckPage(data);
      }).catch(function (err) {
        PbeApp.hidePageLoading();
        submitBtn.removeAttribute('data-kt-indicator');
        submitBtn.disabled = false;
        Swal.fire({
          title: 'Error',
          html: '<p class="pt-8 px-12 fw-semibold fs-6 text-danger">An error ocurred: '+err.status+' '+err.statusText+'</p>',
          buttonsStyling: false,
          confirmButtonText: "Close",
          customClass: {
            confirmButton: "btn btn-sm btn-secondary"
          }
        });
      });
    });

    ackPrintBtn.addEventListener('click', function (e) {
      e.preventDefault();
      if ($(this).data('printElement') !== undefined ) {
        var toPrintElems = [];
        $(this).data('printElement').split(",").forEach(function(c){
          var elems = document.querySelectorAll(c.trim());
          if(elems.length){
            elems.forEach(function(el){
              toPrintElems.push($(el));
            });
          }
        });
        
        if(toPrintElems.length){
          PbeApp.cssPrint('Save as Favourite', null, null, toPrintElems);
        }
      }
    });

    $('.editableInput').off().on('input', (e)=>{
      const rowData = detailsTable.row($(e.target).closest("tr")).data();
      if(rowData.hasOwnProperty(e.target.name)){
        rowData[e.target.name] = e.target.value;
      }
    });
  }

  const initDetailsPage = function () {
    const fields = [{
      title: "Beneficiary Account",
      className: "fw-bold fs-6 text-gray-800 min-win-175px text-center",
      width:200,
      data: function (data) {
          return '<span class="fw-semibold">' + data.toAccNo + '</span>';
      }
    },{
      title: "Beneficiary Name",
      className: "fw-bold fs-6 text-gray-800 min-w-175px text-center",
      width: 200,
      data: function (data) {
        return '<span class="fw-semibold">' + PbeApp.dashIfEmpty(data.toAccName) + '</span>';
      }
    },{
      title: "Favourite Account Name",
      className: "fw-bold fs-6 text-gray-800 min-w-175px text-center",
      width:200,
      data: function (data) {
        const value = (data.name) ? data.name : '';
        return '<div class="fv-row">'
        + '<input type="text" class="form-control editableInput" placeholder="Enter Favourite Account Name" autocomplete="off" spellcheck="false" autocorrect="off" name="name" value="' + value + '"/>'
        + '</div>';
      }
    }, {
      title: "Group Name",
      className: "fw-bold fs-6 text-gray-800 w-200px text-center",
      data: function (data) {
        var html = '<select  class="form-select groupId" data-control="select2" data-hide-search="true" data-placeholder="Select Group Name" name="groupId">';
        html += '<option></option>';
        for(var key in data.groupSelections){
          var selected = (data.groupId == key) ? "selected": "";
          html += '<option value="'+key+'" '+selected+'>' + data.groupSelections[key] + '</option>';
        }
        html += '</select>';
        return '<div class="fv-row mt-0 w-200px">' + html + '</div>' +
          '<input type="hidden" name="toAccNo" value="' + data.toAccNo + '"/>' +//BS4
          '<input type="hidden" name="toAccName" value="' + data.toAccName + '"/>' +//BS4
          '<input type="hidden" name="toAccBank" value="' + data.toAccBankCode + '"/>';//BS4
      }
    },{
      title: "From Account No",
      className: "fw-bold fs-6 text-gray-800 w-250px text-center",
      data: function (data) {
        var html = '<select  class="form-select fromAccNo" data-control="select2" data-hide-search="true" data-placeholder="Select From Acc No." name="frmAccNo">';
        html += '<option></option>';
        html += '</select>';
        return '<div class="fv-row mt-0 w-250px">' + html + '</div>';//BS4
      }
    },{
      title: "Mobile No 1",
      className: "fw-bold fs-6 text-gray-800 w-125px text-center",
      width: 125,
      data: function (data) {
        const value = (data.sms1) ? data.sms1 : '';
        return '<div class="fv-row w-125px">'
        + '<input type="text" class="form-control editableInput" placeholder="Enter Mobile No 1" autocomplete="off" spellcheck="false" autocorrect="off" name="sms1" value="' + value + '"/>'
        + '</div>';
      }
    }, {
      title: "Mobile No 2",
      className: "fw-bold fs-6 text-gray-800 w-125px text-center",
      width: 125,
      data: function (data) {
        const value = (data.sms2) ? data.sms2 : '';
        return '<div class="fv-row w-125px">'
        + '<input type="text" class="form-control editableInput" placeholder="Enter Mobile No 2" autocomplete="off" spellcheck="false" autocorrect="off" name="sms2" value="' + value + '"/>'
        + '</div>';
      }
    },{
      title: "Email 1",
      className: "fw-bold fs-6 text-gray-800 w-125px text-center",
      width: 125,
      data: function (data) {
        const value = (data.email1) ? data.email1 : '';
        return '<div class="fv-row w-125px">'
        + '<input type="text" class="form-control editableInput" placeholder="Enter Email 1" autocomplete="off" spellcheck="false" autocorrect="off" name="email1" value="' + value + '"/>'
        + '</div>';
      }
    },{
      title: "Email 2",
      className: "fw-bold fs-6 text-gray-800 w-125px text-center",
      width: 125,
      data: function (data) {
        const value = (data.email2) ? data.email2 : '';
        return '<div class="fv-row w-125px">'
        + '<input type="text" class="form-control editableInput" placeholder="Enter Email 2" autocomplete="off" spellcheck="false" autocorrect="off" name="email2" value="' + value + '"/>'
        + '</div>';
      }
    }];

    detailsTable = $(detailsTableContainer).DataTable({
      data: tableDefaultData,
      retrieve: true,
      ordering: false,
      orderable: false,
      filter: false,
      info: false,
      paging: false,
      autoWidth: true,
      columns: fields,
      order: [0, 'asc'],
      scrollY: '320px',
      scrollX: true,
      scrollCollapse: true,
      fixedColumns: {
        left: 1
      },
      initComplete: function(settings, json) {
        const t = this.api().table();

        initFavoriteFormValidation();
        initFormEventListeners();

        $('.groupId').on('select2:select', function(e) {
  		  const groupData = e.params.data;
            t.row($(e.target).closest("tr")).data().groupId = groupData.id;
            t.row($(e.target).closest("tr")).data().groupName = groupData.text;

            PbeApp.showPageLoading();

            ajaxGetFromAccNo(groupData.id).then((data) => {
              PbeApp.hidePageLoading();

              var options = [];
              $.each(data, function (i, val) {
                options.push({
                  id: val.fromAccNo,
                  text: val.fromAccNo + " / " + val.name
                });
              });
              
              var fromAccNoSelect = $(this).closest('tr').find('.fromAccNo');
//              if ($(fromAccNoSelect).hasClass("select2-hidden-accessible")) {
//            	  $(fromAccNoSelect).select2("destroy");
//	          }
              
              favorite
	
              $(fromAccNoSelect).select2({
            	  data: options
              });
	
	          $(fromAccNoSelect).on('select2:select', function (evt) {
	              const frmAccNoData = evt.params.data;
	              t.row($(evt.target).closest("tr")).data().frmAccNo = frmAccNoData.id;
	          });

              const fv = favoriteInputFormValidator;
              fv.resetForm();
              
              revalidateFields();
              
            }).catch(function (err) {
              PbeApp.hidePageLoading();
              Swal.fire({
                title: 'Error',
                html: '<p class="pt-8 px-12 fw-semibold fs-6 text-danger">An error ocurred: ' + err.status + ' ' + err.statusText + '</p>',
                buttonsStyling: false,
                confirmButtonText: "Close",
                customClass: {
                  confirmButton: "btn btn-sm btn-secondary"
                }
              });
            });
        }) 
      }
    });

    $('#kt_app_sidebar_toggle').off().on('click', (e)=>{
      setTimeout(() => {
        KTUtil.resize();
        detailsTable.columns.adjust().draw();
      }, 300);
    });
  }

  const initConfirmationPage = function () {
    submitBtn.disabled = false;
    submitBtn.classList.remove('d-none');

    if (confirmationTable) {
      confirmationTable.destroy();
    }

    confirmationTable = $(confirmationTableContainer).DataTable({
      data: detailsTable.rows().data(),
      retrieve: true,
      ordering: false,
      orderable: false,
      filter: false,
      info: false,
      paging: false,
      autoWidth: true,
      columns: dataTableFields,
      order: [0, 'asc'],
      scrollY: '320px',
      scrollX: true,
      scrollCollapse: true,
      fixedColumns: {
        left: 1
      }
    });
  }

  const initAckPage = function (data) {
    if (ackTable) {
      ackTable.destroy();
    }

    ackTable = $(ackTableContainer).DataTable({
      ordering: false,
      orderable: false,
      filter: false,
      info: false,
      paging: false,
      autoWidth: true,
      columns: dataTableFields,
      order: [0, 'asc'],
      scrollY: '320px',
      scrollX: true,
      scrollCollapse: true,
      fixedColumns: {
        left: 1
      }
    });

    ackTable.clear().rows.add(data.favoritesList).draw();
    $('#ack-tab').find("div.submittedDateTimeNice").html(data.submittedDateNice);
    $('#ack-tab').find("div.submitStatus").html(data.status == 1 ? "Pending Approver" : "Failed");
  }

  const revalidateFields = function() {
	     const selects =  favoriteInputForm.querySelectorAll('select');
         selects.forEach(selectEl => {
           var selectField = jQuery(selectEl);
           selectField.on('change.select2', function () {
             var fv = favoriteInputFormValidator;
             if(fv){
             	fv.revalidateField(selectEl.name);
     		   const container = $(selectField).next('.select2-container');
    		   console.log(selectField);
    		   $(selectEl).removeClass('is-invalid');
    		   container.find('.select2-selection').removeClass('is-invalid')
             }
           });
         });
	  
  }
  const ajaxGetFromAccNo = function (groupId) {
    return $.ajax({
      url: 'fundtransfer/rpp/getfrmAccNo',
      type: "POST",
      cache: false,
      dataType: 'json',
      data: {
        groupId: groupId
      }
    });
  }

  const ajaxSubmit = function(formData){
    return $.ajax({
      url: processUrl,
      type: "POST",
      cache: false,
      contentType	: "application/json; charset=utf-8",
      dataType: "json",
      data: JSON.stringify({
        favoritesList: formData,
        status:"Test"
      }),
    });
  }

  const favoriteVerifier = function(data) {
    return $.ajax({
      url: 'fundtransfer/'+tranType+'/favorite/accNoVerifier',
      type: "POST",
      cache: false,
      contentType: "application/json; charset=utf-8",
      dataType: 'json',
      data: JSON.stringify(data)
    });
  }

  const initFavoriteFormValidation = function () {
    var validatorFields = {
      groupId: {
        validators: {
          notEmpty: {
            message: 'Group Name is required.'
          }
        }
      },
      frmAccNo: {
        validators: {
        	notEmpty: {
                message: 'From Account is required.'
              }
        }
      },
      name: {
        validators: {
          notEmpty: {
            message: 'Favourite Name is required.'
          },
          stringLength: {
            max: 20,
            message: 'Favourite Name cannot exceeds 20 characters.'
          },
          regexp: {
              regexp: /^[a-zA-Z0-9\s]*$/,
              message: 'Please enter alphanumeric characters.'
          }
        }
      },
      email1: {
        validators: {
			stringLength: {
				max: 50,
				message: 'The email address cannot exceeds 50 characters.'
			},
			regexp: {
				regexp: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
				message: 'Please enter a valid email address.'
			},
			blank: {}
		}
      },
      email2: {
        validators: {
			stringLength: {
				max: 50,
				message: 'The email address cannot exceeds 50 characters.'
			},
			regexp: {
				regexp: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
				message: 'Please enter a valid email address.'
			},
			blank: {}
		}
      },
      sms1: {
        validators: {
          digits: {
            message: 'Please enter only digits.'
          },
          stringLength: {
            min: 10,
            max: 15,
            message: 'Please enter at least 10 digits.'
          },
          regexp: {
            regexp: /^\S*$/,
            message: 'No spaces allowed.'
          }
        }
      },
      sms2: {
        validators: {
          digits: {
            message: 'Please enter only digits.'
          },
          stringLength: {
            min: 10,
            max: 15,
            message: 'Please enter at least 10 digits.'
          },
          regexp: {
            regexp: /^\S*$/,
            message: 'No spaces allowed.'
          }
        }
      }
    }
    
    favoriteInputFormValidator = FormValidation.formValidation(favoriteInputForm, {
      fields: validatorFields,
      plugins: {
        trigger: new FormValidation.plugins.Trigger(),
        bootstrap: new FormValidation.plugins.Bootstrap5({
          rowSelector: '.fv-row',
		  eleInvalidClass: 'is-invalid',
          eleValidClass: ''
        })
      }
    });
  }

  return {
    //Public Functions
    init: function (config) {
      tranType = config.tranType;
      processUrl = config.processUrl;
      tableDefaultData = config.rowData;

      stepperEl = document.getElementById("fund_transfer_save_as_favorite-stepper");
      
      detailsTableContainer = stepperEl.querySelector('table#tranTable');
      confirmationTableContainer = stepperEl.querySelector('table#confTable');
      ackTableContainer = stepperEl.querySelector('table#ackTable');

      favoriteInputForm = stepperEl.querySelector('#savedTrxForm');

      initNextBtn = stepperEl.querySelector('#initNextBtn');
      submitBtn = stepperEl.querySelector('#submitBtn');
      ackPrintBtn = stepperEl.querySelector("#ackPrintBtn");

      initStepper();
    }
  };
}();
