import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:select_dialog/select_dialog.dart';
import 'package:flutter_settings_ui/flutter_settings_ui.dart';

import 'model/configmodel.dart';

// 참고 사이트 https://pub.dev/packages/settings_ui
class SettingsView extends StatefulWidget {
  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView> with WidgetsBindingObserver {
  @override
  void deactivate() {
    super.deactivate();
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance?.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch(state) {
      case AppLifecycleState.resumed:
      debugPrint("AppLifecycleState.resumed");
        break;
      case AppLifecycleState.inactive:
        debugPrint("AppLifecycleState.inactive");
        ConfigModel configmodel = Provider.of<ConfigModel>(context, listen: false);
        configmodel.updateToSharedPref();
        break;
      case AppLifecycleState.paused:
      debugPrint("AppLifecycleState.paused");
        break;
      case AppLifecycleState.detached:
      debugPrint("AppLifecycleState.detached");
        break;
    }
  }

  void _selectFillTypeDialog(ConfigModel configModel) {
    final types = configModel.getFillTypeTexts();

    String sel = types[configModel.fillType];
    SelectDialog.showModal<String>(
      context,
      showSearchBox: false,
      label: "Fill type",
      selectedValue: sel,
      items: types,
      onChange: (String selected) {
        setState(() {
          var index = types.indexOf(selected);
          configModel.fillType = index;
        });
      },
    );
  }

  void _selectTimeDialog(ConfigModel configModel) {
    final types = configModel.getTimeTypeTexts();

    String sel = types[configModel.timeType];
    SelectDialog.showModal<String>(
      context,
      showSearchBox: false,
      label: "TimeStep type",
      selectedValue: sel,
      items: types,
      onChange: (String selected) {
        setState(() {
          var index = types.indexOf(selected);
          configModel.timeType = index;
        });
      },
    );
  }

  Widget _mainWidget(BuildContext context, ConfigModel configModel) {
    int pictureCount = configModel.pictures.length;

    return Container(
        padding: EdgeInsets.all(4),
        child: SettingsList(
          sections: [
            SettingsSection(
              title: 'Section',
              tiles: [
                SettingsTile(
                  title: '사진 ...',
                  subtitle: '$pictureCount 개의 사진이 선택되어 있습니다.',
                  onPressed: (BuildContext context) {
                    Navigator.pushNamed(context, '/select');
                  },
                ),
                SettingsTile.switchTile(
                    title: '랜덤 순서',
                    onToggle: (value) {
                      setState(() {
                        configModel.suffle = value;
                      });
                    },
                    switchValue: configModel.suffle),
                SettingsTile(
                    title: '화면 맞춤',
                    subtitle: configModel.getSelectedFillTypeText(),
                    onPressed: (context) {
                      _selectFillTypeDialog(configModel);
                    },
                    iosChevron: null),
                SettingsTile(
                    title: '시간 간격',
                    subtitle: configModel.getSelectedTimeSec(),
                    onPressed: (context) {
                      _selectTimeDialog(configModel);
                    },
                    iosChevron: null),
                // SettingsTile(
                //     title: '홈화면 배경으로 지정',
                //     onPressed: (context) {},
                //     iosChevron: null)
              ],
            ),
            SettingsSection(
              title: 'Dev',
              tiles: [
                SettingsTile(
                    title: '모든 설정 초기화',
                    onPressed: (context) {
                      configModel.clearSharedPref();
                      configModel.update();
                    },
                    iosChevron: Icon(Icons.clear)),
                // SettingsTile(
                //     title: '저장된 정보 Console 에 출력',
                //     onPressed: (context) {
                //       String jsonText = configModel.settingsToJson();
                //       debugPrint(jsonText);
                //     },
                //     iosChevron: Icon(Icons.developer_board)),
              ],
            ),
            SettingsSection(
              title: 'About',
              tiles: [
                SettingsTile(
                    title: '이 앱에 대하여 ...',
                    onPressed: (context) {
                      Navigator.pushNamed(context, '/about');
                    },
                    iosChevron: null),
                // SettingsTile(title: '개발자 홈페이지', iosChevron: null)
              ],
            ),
          ],
        ));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Settings')),
      body: SafeArea(child: Consumer<ConfigModel>(
        builder: (context, model, child) {
          return _mainWidget(context, model);
        },
      )),
    );
  }
}
