import 'package:flutter/material.dart';

class DialogUtil {
  static void showAlert(BuildContext context, String title, String message,
      Function onYes, Function onNo, Function? onCancel) {
    var actionWidgets = <Widget>[];
    actionWidgets.add(
      ElevatedButton(
        child: Text("YES"),
        onPressed: () {
          onYes();
          Navigator.of(context).pop();
        },
      ),
    );

    actionWidgets.add(ElevatedButton(
      child: Text("NO"),
      onPressed: () {
        onNo();
        Navigator.of(context).pop();
      },
    ));

    if (onCancel != null) {
      actionWidgets.add(
        ElevatedButton(
          child: Text("CANCEL"),
          onPressed: () {
            onCancel();
            Navigator.of(context).pop();
          },
        ),
      );
    }

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(title),
          content: Text(message),
          actions: actionWidgets,
        );
      },
    );
  }
}
