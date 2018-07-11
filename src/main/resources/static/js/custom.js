$(document).ready(function () {

    var resourceOffset = 0;
    var resourcesTotal = 0;
    var sorting = {
        'column': 'name',
        'dir': 'asc'
    };

    // Loader
    var loader = {
        show: function () {
            $('div#loader').removeClass('hidden');
        },
        hide: function () {
            $('div#loader').addClass('hidden');
        }
    };

    if (typeof directoryId !== 'undefined') {
        loadDirectory(directoryId);
    }

    // Drive alert success
    var driveAlertSuccess = {
        show: function () {
            $('#drive-alert-success').slideDown(100);
        },
        hide: function () {
            $('#drive-alert-success').slideUp(100);
        },
        setText: function (text) {
            $('span#drive-alert-success-text').html(text);
            this.show();
            setTimeout(function () {
                driveAlertSuccess.hide()
            }, 10000);
        }
    };

    var driveAlertDanger = {
        show: function () {
            $('#drive-alert-danger').slideDown(100);
        },
        hide: function () {
            $('#drive-alert-danger').slideUp(100);
        },
        setText: function (text) {
            $('span#drive-alert-danger-text').html(text);
            this.show();
            setTimeout(function () {
                driveAlertSuccess.hide()
            }, 10000);
        }
    };

    // Rename
    function openRenameModal(resourceId) {
        $.ajax({
            url: '/api/resource/' + resourceId,
            type: 'GET',
            beforeSend: function () {
                loader.show();
            },
            success: function (resource) {
                $("span#rename-name").text(resource.name);
                $("div#renameModal form input[name='id']").val(resourceId);
                $("div#renameModal form input[name='name']").val('');
                $("form[name='resource-rename'] div.alert").addClass('hidden');
                $('div#renameModal').modal('show');
            },
            complete: function () {
                loader.hide();
            }
        });
    }

    $("form[name='resource-rename']").submit(function (e) {
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            url: "/api/resource",
            type: "PUT",
            data: formData,
            success: function (response) {
                loadDirectory(directoryId);
                driveAlertSuccess.setText(escapeHtml(response));
                $('#renameModal').modal('hide');
            },
            error: function (response) {
                var alert = $("form[name='resource-rename'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            }
        });
    });

    // Delete file
    function openDeleteModal(resourceId) {
        $.ajax({
            url: '/api/resource/' + resourceId,
            type: 'GET',
            beforeSend: function () {
                loader.show();
            },
            success: function (resource) {
                var modalClass = resource.type === "DIRECTORY" ? "#deleteDirectoryModal" : "#deleteFileModal";
                $(modalClass + " form input[name='id']").val(resourceId);
                $(modalClass + " span.resource-name").text(resource.name);
                $(modalClass).modal('show');
            },
            complete: function () {
                loader.hide();
            }
        });
    }

    $("form[name='delete-file").submit(function (e) {
        e.preventDefault();
        $.ajax({
            url: "/api/resource/" + $("div#deleteFileModal form input[name='id']").val(),
            type: "DELETE",
            success: function (response) {
                loadDirectory(directoryId);
                $('#deleteFileModal').modal('hide');
                driveAlertSuccess.setText(response);
            },
            error: function (response) {
                var alert = $("form[name='delete-file'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            }
        });
    });

    $("form[name='delete-directory").submit(function () {
        $.ajax({
            url: "/api/resource/" + $("div#deleteDirectoryModal form input[name='id']").val(),
            type: "DELETE",
            success: function (response) {
                loadDirectory(directoryId);
                $('#deleteDirectoryModal').modal('hide');
                driveAlertSuccess.setText(escapeHtml(response));
            },
            error: function (response) {
                var alert = $("form[name='delete-directory'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            }
        });
        return false;
    });

    $('body').on('click', 'a.open-file', function () {
        var id = $(this).attr("data-id");
        var loading = $("div#file-loading");
        var alert = $("div#openFileModal .alert");
        alert.hide();
        $("a#download-link").attr("href", "/download/" + id);
        loading.show();
        $("div#openFileModal .progress").show();
        var progressBar = $("div#openFileModal .progress-bar");
        progressBar.width("0%");
        progressBar.text("0%");
        var content = $("#file-content");
        content.text("");
        $.ajax({
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.addEventListener('progress', function (e) {
                    if (e.lengthComputable) {
                        var percentages = Math.round((e.loaded / e.total) * 100) + '%';
                        progressBar.width(percentages);
                        progressBar.text(percentages);
                    }
                });
                return xhr;
            },
            url: "/api/file/" + id,
            success: function (response) {
                loading.hide();
                $("div#openFileModal .progress").hide();
                $("#file-name").text(escapeHtml(response.name));
                if (isImage(response.mimeType)) {
                    content.html('<img class="preview-image img-rounded" src="' + "/download/" + id + '" alt="' + escapeHtml(response.name) + '" />');
                }
                else if (isCode(response.mimeType)) {
                    content.html("<code>" + allowNewlines(escapeHtml(response.content)) + "</code>");
                }
                else {
                    content.text(messages.fileNotReadable);
                }
            },
            error: function (response) {
                alert.html(response.responseText);
                alert.show();
            }
        })
    });

    // Create directory - form submit
    var createDirectorySubmitButton = $('form[name="directory-create"] button[type="submit"]');
    $("form[name='directory-create']").ajaxForm({
        beforeSend: function () {
            createDirectorySubmitButton.addClass('disabled');
        },
        success: function (xhr) {
            loadDirectory(directoryId);
            driveAlertSuccess.setText(escapeHtml(xhr));
            $('form[name="directory-create"] button[type="reset"]').click();
        }
    });

    // Upload file - form submit
    var uploadProgressBar = $('div#uploadFileModal .progress-bar');
    var uploadFileAlert = $("div#upload-file-alert");
    $('form[name=\'file-upload\']').ajaxForm({
        type: 'post',
        beforeSend: function () {
            // Disable all elements
            $('form[name="file-upload"] button[type="submit"]').addClass('disabled');
            uploadFileAlert.addClass('hidden');

            // Set progress bar
            var percentVal = '0%';
            uploadProgressBar.width(percentVal);
            uploadProgressBar.text(percentVal);
            $('div#uploadFileModal .progress').removeClass('hidden');
        },
        uploadProgress: function (event, position, total, percentComplete) {
            var percentVal = percentComplete + '%';
            uploadProgressBar.width(percentVal);
            uploadProgressBar.text(percentVal);
        },
        complete: function (xhr) {
            if (xhr.status === 200) {
                loadDirectory(directoryId);
                driveAlertSuccess.setText(xhr.responseText);
                $('#uploadFileModal').modal('hide');
            }
            else {
                uploadFileAlert.html('<div class="alert alert-danger">' + xhr.responseText + '</div>');
                uploadFileAlert.removeClass('hidden');
            }
            $('div#uploadFileModal .progress').addClass('hidden');
        }
    });

    // Drive - right click context menu
    $.contextMenu({
        selector: '.context-menu-one',
        callback: function (key, options) {
            var resourceId = options.$trigger.context.dataset.id;
            switch (key) {
                case "rename":
                    openRenameModal(resourceId);
                    break;
                case "delete":
                    openDeleteModal(resourceId);
                    break;
                case "share":
                    openShareModal(resourceId);
                    break;
                case "download":
                    download(resourceId);
                    break;
            }
        },
        items: {
            "rename": {name: messages.rename, icon: "pencil"},
            "delete": {name: messages.delete, icon: "trash"},
            "share": {name: messages.share, icon: "share"},
            "download": {name: messages.download, icon: "cloud-download"}
        }
    });

    // Check all
    $("#check-all").click(function () {
        var ATTR = "data-checked";
        var button = $("#delete-checked");
        if ($(this).attr(ATTR) === "" || $(this).attr(ATTR) === undefined) {
            setChecked = true;
            button.removeClass("disabled");
            $(this).attr(ATTR, "true");
        }
        else {
            setChecked = false;
            button.addClass("disabled");
            $(this).attr(ATTR, "");
        }

        $("input[name='resources[]']").each(function (i, item) {
            item.checked = setChecked;
        });
    });

    // Delete all
    function getCheckedResources() {
        var items = [];
        $("input[name='resources[]']").each(function (i, item) {
            if (item.checked) {
                items.push($(this));
            }
        });
        return items;
    }

    $("input[name='resources[]").change(function () {
        var button = $("#delete-checked");
        if (getCheckedResources().length === 0) {
            button.addClass("disabled");
        }
        else {
            button.removeClass("disabled");
        }
    });
    $("#delete-checked").click(function () {
        var list = $("#resources-list");
        list.text("");

        var items = getCheckedResources();
        if (items.length === 0) {
            return false;
        }

        items.forEach(function (item, i) {
            var id = item.val();
            var name = item.attr("data-name");
            var type = item.attr("data-type");
            var iconClass = type === "DIRECTORY" ? "glyphicon glyphicon-folder-open" : "glyphicon glyphicon-file";
            var html = "";
            html += ('<div>');
            html += ('<span class="' + iconClass + '"></span>');
            html += ('&nbsp;' + name);
            html += ('</div>');
            html += '<input type="hidden" name="ids[]" value="' + id + '"/>';
            list.append(html);
        });
    });
    $("form[name='delete-checked']").submit(function () {
        var formData = $(this).serialize();
        $.ajax({
            url: "/api/resource/deleteChecked",
            type: "POST",
            data: formData,
            beforeSend: function () {
                loader.show();
            },
            success: function () {
                location.reload();
            },
            error: function (response) {
                var alert = $("form[name='resource-checked'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            },
            complete: function () {
                loader.hide();
            }
        });
        return false;
    });

    // Search
    $('form[name="search"]').ajaxForm({
        type: 'GET',
        beforeSend: function () {
            loader.show();
        },
        complete: function (xhr) {
            loader.hide();
            var content = $('table#resources tbody');
            var contentEmpty = $('div#resources-empty');
            var table = $('table#resources');
            var addButtons = $('button.add-resource');

            table.addClass('hidden');
            contentEmpty.addClass('hidden');
            addButtons.addClass('disabled');
            $('form[name="search"] button[type="reset"]').removeClass('hidden');
            loadBreadcrumbs([]);

            var resources = xhr.responseJSON;
            if (resources.length === 0) {
                contentEmpty.removeClass('hidden');
            }
            else {
                content.html(getResourcesHtml(resources));
                table.removeClass('hidden');
            }
        }
    });
    $('form[name="search"] button[type="reset"]').click(function () {
        $('form[name="search"] input').val('');
        loadDirectory(null);
        $(this).addClass('hidden');
    });

    $(window).scroll(function () {
        if ($(window).scrollTop() + $(window).height() === $(document).height() && resourceOffset < resourcesTotal) {
            loadMore(directoryId);
        }
    });

    // Ajax directory listing
    function printDirectory(directory, resources, breadcrumbs) {
        var content = $('table#resources tbody');
        var contentEmpty = $('div#resources-empty');
        var table = $('table#resources');
        var addButtons = $('button.add-resource');

        table.addClass('hidden');
        contentEmpty.addClass('hidden');
        addButtons.removeClass('disabled');
        $('form[name="search"] input').val('');
        $('form[name="search"] button[type="reset"]').addClass('hidden');

        if (directory === null) {
            return;
        }

        directoryId = directory.id;
        insertParam('dir', directory.id);
        $('input[name="parent_id"]').val(directoryId);

        // Handle breadcrumbs
        loadBreadcrumbs(breadcrumbs);

        if (resources.length === 0) {
            contentEmpty.removeClass('hidden');
            return;
        }


        content.html(getResourcesHtml(resources));
        table.removeClass('hidden');

    }

    $('button#add-directory').click(function () {
        createDirectorySubmitButton.removeClass('disabled');
        $('form[name="directory-create"] input[name="name"]').val('');
        $('form[name="directory-create"] .alert').text('').addClass('hidden');
    });

    $('button#upload-file').click(function () {
        $('form[name="file-upload"] .alert').addClass('hidden');
        $('form[name="file-upload"] input[name="file"]').val('');
        $('form[name="file-upload"] button[type="submit"]').removeClass('disabled');
    });

    function loadBreadcrumbs(breadcrumbs) {
        var breadcrumbsContainer = $('ol#breadcrumbs');
        var breadcrumbsHtml = '<li><a href="/" class="open-directory" data-id=""><span class="glyphicon glyphicon-home"></span></a>';
        $.each(breadcrumbs, function (i, item) {
            var last = i === breadcrumbs.length - 1;
            breadcrumbsHtml += '<li class="' + (last ? 'active' : '') + '">';
            if (last) {
                breadcrumbsHtml += '<span>' + escapeHtml(item.name) + '</span>';
            }
            else {
                breadcrumbsHtml += '<a href="#" class="open-directory" data-id="' + item.id + '">' + escapeHtml(item.name) + '</a>';
            }
            breadcrumbsHtml += '</li>';
        });
        breadcrumbsContainer.html(breadcrumbsHtml);
    }

    function getResourcesHtml(resources) {
        var html = '';
        $.each(resources, function (i, resource) {
            html += '<tr class="context-menu-one resource" data-id="' + resource.id + '">';

            html += '<td>';
            if (resource.type === 'DIRECTORY') {
                html += '<a href="/?dir=' + resource.id + '" class="open-directory" data-id="' + resource.id + '">';
                html += '<span class="glyphicon glyphicon-folder-open"></span>';
            }
            else {
                html += '<a class="open-file" data-id="' + resource.id + '" data-toggle="modal" data-target="#openFileModal">';
                html += '<span class="glyphicon glyphicon-file"></span>';
            }
            html += '&nbsp;' + escapeHtml(resource.name);
            html += '</a>';
            html += '</td>';

            html += '<td>';
            html += formatDatetime(resource.modificationTime);
            html += '</td>';

            html += '<td>' + resource.friendlySize + '</td>';

            html += '<td>';
            html += '<input ' +
                'type="checkbox" ' +
                'name="resources[]" ' +
                'value="' + resource.id + '" ' +
                'data-name="' + escapeHtml(resource.name) + '" ' +
                'data-type="' + resource.type + '" ' +
                '/>';
            html += '</td>';

            html += '</tr>';
        });
        return html;
    }

    function loadDirectory(directoryId) {
        var url = "/api/directory/details/" + (directoryId != null ? directoryId : '0');
        url += "?sortColumn=" + sorting.column + "&sortDir=" + sorting.dir;
        loader.show();
        $.ajax({
            url: url,
            type: "GET",
            complete: function () {

            },
            success: function (response) {
                printDirectory(response.directory, response.resources, response.breadcrumbs);
                updateDriveSpaceBar(response.spaceUsed, response.spaceAvailable, response.spaceUsedPercentages);
                resourceOffset += response.resources.length;
                resourcesTotal = response.total;
                loader.hide();
            },
            error: function (response) {
                var alert = $("form[name='resource-checked'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            }
        });
    }

    function loadMore(directoryId) {
        var url = "/api/directory/more/" + (directoryId != null ? directoryId : '0');
        url += "?sortColumn=" + sorting.column + "&sortDir=" + sorting.dir;
        url += "&offset=" + resourceOffset;
        loader.show();
        $.ajax({
            url: url,
            type: "GET",
            success: function (resources) {
                var content = $('table#resources tbody');
                content.append(getResourcesHtml(resources));
                resourceOffset += resources.length;
                loader.hide();
            },
            error: function (response) {
                var alert = $("form[name='resource-checked'] div.alert");
                alert.html(response.responseText);
                alert.removeClass('hidden');
            }
        });
    }

    $('body').on('click', 'a.open-directory', function () {
        var directoryId = $(this).attr("data-id");
        loadDirectory(directoryId);
        return false;
    });

    $('div#drive-alert-success span.glyphicon.glyphicon-remove, div#drive-alert-danger span.glyphicon.glyphicon-remove').click(function () {
        $(this).parent().slideUp();
    })

    function updateDriveSpaceBar(spaceUsed, spaceAvailable, spaceUsedPercentages) {
        $('span#spaceUsed').text(spaceUsed);
        $('span#spaceAvailable').text(spaceAvailable);
        var spaceBarProgress = $('div#space-bar div');
        spaceBarProgress.attr('class', getProgressClass(spaceUsedPercentages));
        spaceBarProgress.text(spaceUsedPercentages + '%');
        spaceBarProgress.attr('style', 'min-width: 2em;width:0%');
        for (var i = 0; i < spaceUsedPercentages; i++) {
            setTimeout(function () {
                spaceBarProgress.attr('style', 'min-width: 2em;width:' + i + '%');
            }, 100)
        }
    }

    function getProgressClass(percentage) {
        if (percentage <= 50) {
            return "progress-bar progress-bar-success";
        } else if (percentage > 50 && percentage < 75) {
            return "progress-bar progress-bar-warning";
        }
        return "progress-bar progress-bar-danger";
    }

    // Drive sorting
    $('span.sort').click(function () {
        $('span.sort').removeClass('active');
        $(this).addClass('active');
        sorting = {
            column: $(this).attr('data-column'),
            dir: $(this).attr('data-dir')
        };
        loadDirectory(directoryId);
    });

    // Share modal
    function openShareModal(resourceId) {
        $('div#shareModal .alert').addClass('hidden');
        $('div#shareModal .form-group').removeClass('hidden');
        $('div#shareModal button[type="submit"]').removeClass('hidden');
        $.ajax({
            url: '/api/resource/' + resourceId,
            type: 'GET',
            beforeSend: function () {
                loader.show();
            },
            success: function (resource) {
                if (resource.type === 'FILE') {
                    var modalClass = "#shareModal"
                    $(modalClass + " form input[name='id']").val(resourceId);
                    $(modalClass + " form input[name='type']").val('');
                    $(modalClass + " .modal-title").text(resource.name);
                    $(modalClass).modal('show');
                }
                else {
                    driveAlertDanger.setText(messages.onlyFilesCanBeShared);
                }
            },
            complete: function () {
                loader.hide();
            }
        });
    }

    $('form[name="file-share"]').ajaxForm({
        complete: function (xhr) {
            if (xhr.status === 200) {
                $('div#shareModal .alert-success').html(xhr.responseText).removeClass('hidden');
                $('div#shareModal .form-group').addClass('hidden');
                $('div#shareModal button[type="submit"]').addClass('hidden');
            }
            else {
                $('div#shareModal .alert-danger').html(xhr.responseText).removeClass('hidden');
            }
        }
    })

    function download(resourceId) {
        $.ajax({
            url: '/api/resource/' + resourceId,
            type: 'GET',
            beforeSend: function () {
                loader.show();
            },
            success: function (resource) {
                if (resource.type === 'FILE') {
                    location.href = '/download/' + resourceId;
                }
                else {
                    driveAlertDanger.setText(messages.onlyFilesCanBeSDownloaded);
                }
            },
            complete: function () {
                loader.hide();
            }
        });
    }

});
