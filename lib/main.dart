import 'package:flutter/material.dart';
import 'package:gallerywallpaper/model/configmodel.dart';
import 'package:gallerywallpaper/settingsview.dart';
import 'package:provider/provider.dart';

import 'selectview.dart';
import 'aboutview.dart';

void main() {
  runApp(ChangeNotifierProvider(
      create: (_) {
        var model = ConfigModel();
        model.init();

        return model;
      },
      child: MyApp()));
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: 'Gallery livewallpaper',
        initialRoute: '/',
        onGenerateRoute: (settings) {
          var routes = <String, WidgetBuilder>{
            '/': (context) => SettingsView(),
            '/select': (context) => SelectView(),
            '/about': (context) => AboutView(),
          };

          WidgetBuilder? builder = routes[settings.name];
          return MaterialPageRoute(builder: (ctx) => builder!(ctx));
        });
  }
}

// Method Channel Example
/*
static const platform = const MethodChannel("GalleryWallpaper/test");
String? _dataFromFlutter;

Future<void> _getDataFromAdnroid() async {
  print("calling for data");
  String data;
  try {
    final String result = await platform.invokeMethod(
        'test', {"data": "Call me flutter"}); //sending data from flutter here
    data = result;
  } on PlatformException catch (e) {
    data = "Android is not responding please check the code";
  }

  setState(() {
    _dataFromFlutter = data;
  });
}
*/

