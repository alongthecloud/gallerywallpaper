import 'package:flutter/material.dart';
import 'package:gallerywallpaper/model/configmodel.dart';
import 'package:reorderable_grid/reorderable_grid.dart';
import 'package:uuid_type/uuid_type.dart';

class SelectViewPicture extends StatefulWidget {
  final List<PictureData> pictures;
  final Map<Uuid, bool> picturesSelected;

  SelectViewPicture(this.pictures, this.picturesSelected);

  @override
  State<StatefulWidget> createState() => _SelectViewPictureState();
}

class _SelectViewPictureState extends State<SelectViewPicture> {
  List<Widget> _pictureWidgets(BuildContext context) {
    List<Widget> pictureWidgets = [];

    widget.pictures.forEach((value) {
      bool selected = widget.picturesSelected[value.uuid] ?? false;
      IconData checkIconData =
          selected ? Icons.check_circle_outline : Icons.circle_outlined;
      pictureWidgets.add(Container(
          key: Key(value.uuid.toString()),
          decoration:
              BoxDecoration(border: Border.all(width: 1, color: Colors.grey)),
          child: Stack(alignment: Alignment.center, children: <Widget>[
            value.image!,
            Positioned(
                top: 2,
                right: 2,
                child: IconButton(
                  icon: Icon(checkIconData),
                  color: Colors.black,
                  onPressed: () {
                    setState(() {
                      widget.picturesSelected[value.uuid] = !selected;
                    });
                  },
                ))
          ])));
    });

    return pictureWidgets;
  }

  void _onReorder(int oldIndex, int newIndex) {
    setState(() {
      final item = widget.pictures.removeAt(oldIndex);
      widget.pictures.insert(newIndex, item);
    });
  }

  @override
  Widget build(BuildContext context) {
    var pictureGrid = ReorderableGridView.extent(
      maxCrossAxisExtent: 250,
      onReorder: _onReorder,
      childAspectRatio: 9.0 / 16.0,
      mainAxisSpacing: 1,
      crossAxisSpacing: 1,
      padding: EdgeInsets.all(2),
      children: _pictureWidgets(context),
    );

    return Container(child: pictureGrid);
  }
}
