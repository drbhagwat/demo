$('document').ready(function() {
$('.table .btn').on('click', function(event) {
   event.preventDefault();

   var href = $(this).attr('href');
    $.get(href, function(student, status) {
        $('#idEdit').val(student.id);
        $('#nameEdit').val(student.name);
        $('#departmentEdit').val(student.department);

    });
   $('#editModal').modal();
});
});