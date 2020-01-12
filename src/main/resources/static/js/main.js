$(document).ready(function() {
  $('.new, .table .edit').on('click', function (event) {
    event.preventDefault();
    var href = $(this).attr('href');
    var text = $(this).text();

    if (text == 'Edit') {
      $.get(href, function(company, status) {
        $('.form #id').val(company.id);
        $('.form #name').val(company.name);
        $('.form #description').val(company.description);
      });
    } else {
        $('.form #id').val('');
        $('.form #name').val('');
        $('.form #description').val('');
        $('.form #editModal').modal();
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
