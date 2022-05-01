import 'dart:io';

import 'package:flutter/material.dart';
import 'package:gallerywallpaper/dialogUtil.dart';
import 'package:gallerywallpaper/selectview_picture.dart';
import 'package:provider/provider.dart';
import 'package:media_picker_widget/media_picker_widget.dart';
import 'package:uuid_type/uuid_type.dart';

import 'model/configmodel.dart';

class SelectView extends StatelessWidget {
  Map<Uuid, bool> _pictureSelected = {};

  Future<List<PictureData>> _updatePictureWidgets(BuildContext context) async {
    var configModel = Provider.of<ConfigModel>(context, listen: false);

    for (PictureData data in configModel.pictures) {

      _pictureSelected.putIfAbsent(data.uuid, () {
        var file = File(data.path);
        data.image = Image.file(file);
        return false;
      });
    }

    return configModel.pictures;
  }

  void _removePictures(BuildContext context, ConfigModel configModel) {
    var configModel = Provider.of<ConfigModel>(context, listen: false);

    _pictureSelected.removeWhere((key, value) {
      if (value) {
        configModel.pictures.removeWhere((element) => element.uuid == key);
        return true;
      } else {
        return false;
      }
    });

    configModel.update();
  }

  List<Media> _mediaList = [];

  void _openImagePicker(BuildContext context, ConfigModel configModel) {
    showModalBottomSheet(
        context: context,
        builder: (context) {
          return MediaPicker(
            mediaList: _mediaList,
            onPick: (List<Media> selectedList) {
              _mediaList = selectedList;
              Navigator.pop(context);
              _updatePictures(context, configModel);
            },
            onCancel: () => Navigator.pop(context),
            mediaCount: MediaCount.multiple,
            mediaType: MediaType.image,
            decoration: PickerDecoration(
              actionBarPosition: ActionBarPosition.top,
              blurStrength: 2,
              completeText: 'Next',
            ),
          );
        });
  }

  void _selectPictures(BuildContext context, ConfigModel configModel) {
    if (!Platform.isAndroid) return;

    _openImagePicker(context, configModel);
  }

  void _updatePictures(BuildContext context, ConfigModel configModel) {
    if (_mediaList.length == 0) return;

    _mediaList.forEach((Media element) {
      if (element.file == null) return;

      var imageFile = element.file!;
      // debugPrint(imageFile.path);

      configModel.pictures.add(PictureData(TimeUuidGenerator().generate(), imageFile.path, null));
    });

    _mediaList.clear();
    configModel.update();
  }

  Widget _mainWidget(BuildContext context, ConfigModel configModel) {
    return Container(
        padding: EdgeInsets.all(4),
        child: Column(children: [
          Container(
            child: Row(
              children: [
                Expanded(
                    flex: 4,
                    child: Container(
                      padding: EdgeInsets.fromLTRB(2, 2, 2, 2),
                      child: ElevatedButton(
                          child: Text('Add from Album'),
                          onPressed: () {
                            _selectPictures(context, configModel);
                          }),
                    )),
                /* Expanded(
                    flex: 4,
                    child: Container(
                      padding: EdgeInsets.fromLTRB(2, 2, 2, 2),
                      child: ElevatedButton(
                          child: Text('Add from Web'),
                          onPressed: () {
                            // _selectPictures(context, configModel);
                          }),
                    )), */
                Expanded(
                    flex: 3,
                    child: Container(
                      padding: EdgeInsets.fromLTRB(2, 2, 2, 2),
                      child: ElevatedButton(
                          child: Text('Remove'),
                          onPressed: () {
                            DialogUtil.showAlert(
                                context, 'Alert !', 'delete select pictures', () {
                              _removePictures(context, configModel);
                            }, () {}, null);
                          }),
                    ))
              ],
            ),
          ),
          Expanded(
              child: FutureBuilder(
                  future: _updatePictureWidgets(context),
                  builder: (BuildContext context, AsyncSnapshot snapshot) {
                    if (snapshot.hasData == false) {
                      return CircularProgressIndicator();
                    } else if (snapshot.hasError) {
                      return Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          'Error: ${snapshot.error}',
                          style: TextStyle(fontSize: 15),
                        ),
                      );
                    } else {
                      return SelectViewPicture(snapshot.data, _pictureSelected);
                    }
                  }))
        ]));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text('Select')),
        body: Consumer<ConfigModel>(builder: (context, model, child) {
          return _mainWidget(context, model);
        }));
  }
}
