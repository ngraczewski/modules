(function () {

    'use strict';

    var seuss = angular.module('seuss');

    seuss.controller('SchemaEditorCtrl', function ($scope, Entities) {
        $scope.SELECT_ENTITY_CONFIG = {
            placeholder: $scope.msg('seuss.info.selectEntity'),
            ajax: {
                url: '../seuss/entities',
                dataType: 'json',
                quietMillis: 100,
                data: function (term, page) {
                    return {
                        term: term,
                        pageLimit: 5,
                        page: page
                    };
                },
                results: function (data) {
                    return data;
                }
            },
            initSelection: function(element, callback) {
                var id = $(element).val();

                if (!isBlank(id)) {
                    $.ajax("../seuss/entities/" + id).done(function (data) {
                        callback(data);
                    });
                }
            },
            formatSelection: function (obj) {
                var name = obj && obj.name ? obj.name : '',
                    module = obj && obj.module ? ' {0}: {1}'
                        .format($scope.msg('seuss.module'), obj.module) : '',
                    namespace = obj && obj.namespace ? ' {0}: {1}'
                        .format($scope.msg('seuss.namespace'), obj.namespace) : '',
                    info = $.trim('{0} {1}'.format(module, namespace)),
                    label = !isBlank(info) && !isBlank(name)
                        ? '{0} ({1})'.format(name, info) : !isBlank(name) ? name : '';

                return isBlank(label) ? $scope.msg('seuss.error') : label;
            },
            formatResult: function (obj) {
                var strong = obj && obj.name
                        ? angular.element('<strong>').text(obj.name)
                        : undefined,
                    name = strong
                        ? angular.element('<div>').append(strong)
                        : undefined,
                    module = obj && obj.module
                        ? angular.element('<span>')
                            .text(' {0}: {1}'.format($scope.msg('seuss.module'), obj.module))
                        : undefined,
                    namespace = obj && obj.namespace
                        ? angular.element('<span>')
                            .text(' {0}: {1}'.format($scope.msg('seuss.namespace'), obj.namespace))
                        : undefined,
                    info = (module || namespace)
                        ? angular.element('<div>').append(module).append(namespace)
                        : undefined,
                    parent = (name || info)
                        ? angular.element('<div>').append(name).append(info)
                        : undefined;

                return parent || $scope.msg('seuss.error');
            },
            escapeMarkup: function (markup) {
                return markup;
            }
        };

        $scope.selectedEntity = null;

        $scope.createEntity = function () {
            var form = angular.element("#newEntityModalForm"),
                input = form.find('#inputEntityName'),
                help = input.next('span'),
                value = input.val(),
                obj = {};

            if (isBlank(value)) {
                help.removeClass('hide');
            } else {
                obj.name = value;

                Entities.save({}, obj, function (response) {
                    $scope.selectedEntity = response;
                    angular.element('#selectEntity').select2('val', response.id);

                    $scope.clearModal('newEntityModal');
                }, function (response) {
                    handleResponse('seuss.error', 'seuss.error.cantSaveEntity', response);
                });
            }
        };

        $scope.clearModal = function (modalId) {
            var modal = angular.element('#{0}'.format(modalId)),
                form = modal.find('form'),
                spans = form.find('span.help-block');

            angular.forEach(spans, function (span) {
                var that = angular.element(span);

                if (!that.hasClass('hide')) {
                    that.addClass('hide');
                }
            });

            form.resetForm();
            modal.modal('hide');
        };

    });

    seuss.controller('DataBrowserCtrl', function ($scope) {});

    seuss.controller('SettingsCtrl', function ($scope) {});

}());
