$(document).ready(function() {
  $('.new, .table .edit').on('click', function (event) {
    console.log("Came here");
    event.preventDefault();
    var href = $(this).attr('href');
    var text = $(this).text();

    if (text == 'Edit') {
      $.get(href, function(address, status) {
        $('.form #id').val(address.id);
        $('.form #address1').val(address.address1);
        $('.form #address1').val(address.address2);
        $('.form #city').val(address.city);
        $('.form #state').val(address.state);
        $('.form #zip').val(address.zip);
      });
    } else {
        $('.form #id').val('');
        $('.form #address1').val('');
        $('.form #address1').val('');
        $('.form #city').val('');
        $('.form #state').val('');
        $('.form #zip').val('');
    }
    $('.form #editModal').modal();
  });

  $('.table .delete').on('click', function (event) {
    event.preventDefault();
    var href = $(this).attr('href');
    $('#deleteModal #delRef').attr('href', href);
    $('#deleteModal').modal();
  });
});
