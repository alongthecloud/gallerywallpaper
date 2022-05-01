import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:uuid_type/uuid_type.dart';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PictureData {
  Uuid uuid;
  String path;
  Image? image;

  PictureData(Uuid uuid, String path, Image? image)
  : this.uuid = uuid
  , this.path = path
  , this.image = image
  {
  }
}

class ConfigModel with ChangeNotifier {
  static const Methodcall_pref = const MethodChannel("gallerywallpaper.pref");

  Future<bool> call_save_pref(String jsonText) async {
    bool resultFlag = false;
    if (!Platform.isAndroid) return resultFlag;

    try {
      final bool result =
          await Methodcall_pref.invokeMethod('save', {"text": jsonText});
      resultFlag = result;
    } on PlatformException catch (e) {
      log(e.toString());
      resultFlag = false;
    }

    return resultFlag;
  }

  Future<String> call_load_pref() async {
    String resultText = "";
    if (!Platform.isAndroid) return resultText;

    try {
      final String result = await Methodcall_pref.invokeMethod('load');
      resultText = result;
    } on PlatformException catch (e) {
      log(e.toString());
      resultText = "";
    }

    return resultText;
  }

  final fillTypes_en = <String>['Strech', 'Fit', 'Center'];
  final fillTypes_kr = <String>['채우기', '맞추기', '가운데'];

  final timeType_en = <String>[
    'every 10sec',
    'every 1min',
    'every 1hour',
    'every 1day'
  ];
  final timeType_kr = <String>['매10초', '매분', '매시각', '매일'];
  final timeType_value = <int>[10, 60, 3600, 3600 * 24];

  List<PictureData> pictures = [];
  int fillType = 0;
  bool suffle = false;
  int timeType = 0;
  int timeSec = 60;

  List<String> getFillTypeTexts() {
    return fillTypes_kr;
  }

  String getSelectedFillTypeText() {
    return fillTypes_kr[fillType];
  }

  String getSelectedTimeSec() {
    return timeType_kr[timeType];
  }

  List<String> getTimeTypeTexts() {
    return timeType_kr;
  }

  int timetype2timesec(int type) {
    return timeType_value[type];
  }

  int timesec2timetype(int sec) {
    int type = 0;
    for (int i = 0; i < timeType_value.length; ++i) {
      if (timeType_value[i] == sec) {
        type = i;
        break;
      }
    }
    return type;
  }

  void update() {
    notifyListeners();
    updateToSharedPref();
  }

  void init() async {
    await loadFromSharedPref();
    notifyListeners();
  }

  Future<void> loadFromSharedPref() async {
    String jsonText = await call_load_pref();
    if (jsonText.length == 0) return;

    Map<String, dynamic> jsonObj = new Map<String, dynamic>();
    jsonObj = jsonDecode(jsonText);

    pictures.clear();
    final jsonobjPictures = jsonObj['pictures'];
    for (var obj in jsonobjPictures) {
      pictures.add(PictureData(TimeUuidGenerator().generate(), obj.toString(), null));
    }

    fillType = jsonObj['filltype'] ?? 0;
    suffle = jsonObj['suffle'] ?? false;
    timeSec = jsonObj['timesec'] ?? 0;
    timeType = timesec2timetype(timeSec);

    int jsonTextLength = jsonText.length;
    log("load json data : " + jsonTextLength.toString());
  }

  Future<void> updateToSharedPref() async {
    String jsonText = settingsToJson();
    bool ret = await call_save_pref(jsonText);

    log(jsonText);
    log("call " + ret.toString());
  }

  String settingsToJson() {
    Map<String, dynamic> jsonObj = new Map<String, dynamic>();

    List<String> picturePaths = [];
    for (var pic in pictures) {
      picturePaths.add(pic.path);
    }

    jsonObj['pictures'] = picturePaths;
    jsonObj['filltype'] = fillType;
    jsonObj['suffle'] = suffle;

    timeSec = timetype2timesec(timeType);
    jsonObj['timesec'] = timeSec;

    String jsonText = jsonEncode(jsonObj);
    return jsonText;
  }

  void clearSharedPref() {
    pictures.clear();
    fillType = 0;
    suffle = false;

    updateToSharedPref();
  }
}
