$(function () {
                var currentOption = $('#qselect :selected').attr('value');

                $('#' + currentOption + 'div').clone().attr('id', 'activediv')
                    .appendTo('#extraopts').css('display', 'block');

                $('#qselect').change(function (event) {
                    currentOption = this.options[event.target.selectedIndex].value;
                    $('#activediv').remove();
                    $('#' + currentOption + 'div').clone().attr('id', 'activediv').
                        appendTo('#extraopts').css('display', 'block');
                });

                $('#addMcOpt').click(function (event) {
                    var mcCount = $('#mcOptions').children().size();
                    $('#mcOptions').append($('<input type="text"/>').attr('name', 'mcOpt' + mcCount));
                });
            });
