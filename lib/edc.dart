import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

class Edc {
  static const _methodChannel = MethodChannel("edc");
  static const _eventChannel = EventChannel("edc.eventChannel");
  Completer<dynamic>? _responseCompleter;

  void Function()? callPrint() {
    return () async {
      await _methodChannel.invokeMethod("callPrint", {
        "data_print": EdcPrintPayloadModel(
            namaFitur: "nama fitur",
            isCetakUlang: "false",
            namaAgen: "agen46",
            footerMessage: "footermessage",
            dataList: List.generate(
                5,
                (i) => DataListModel(
                    isLarge: "false", lineString: "Line String"))).toEncode()
      });
    };
  }

  var dummyCallHost = {
    "systemId": "POSTPAID",
    "channelId": "NEWIBANK",
    "clientId": "NEWIBANK",
    "feeId": "00545300",
    "customerId": "3274387492",
    "reffNum": "2348799387492",
    "browser_agent": "android",
    "ip_address": "192.0.0.29",
    "id_api": "mobile",
    "ip_server": "68",
    "req_id": "342424234",
    "session": "4342423",
  };
  Future<dynamic> callHost() async {
    await _methodChannel.invokeMethod(
        "callHost", {'path': "plnInquiry", 'data': dummyCallHost});
    _responseCompleter = Completer<dynamic>();
    _eventChannel.receiveBroadcastStream().listen((event) {
      if (_responseCompleter != null && !_responseCompleter!.isCompleted) {
        _responseCompleter?.complete(event);
        _responseCompleter = null;
      }
    }, onError: (error) {
      if (_responseCompleter != null && !_responseCompleter!.isCompleted) {
        _responseCompleter?.completeError(error);
        _responseCompleter = null;
      }
    });
    return await _responseCompleter?.future;
  }

  void Function()? callPayment() {
    return () async {
      await _methodChannel.invokeMethod("callPaymentApp", {"nominal": "20000"});
    };
  }

  Stream<dynamic> eventChannel() {
    return _eventChannel.receiveBroadcastStream();
  }
}

class EdcPrintPayloadModel {
  String? namaFitur;
  String? isCetakUlang;
  String? namaAgen;
  String? footerMessage;
  List<DataListModel>? dataList;

  EdcPrintPayloadModel({
    this.namaFitur,
    this.isCetakUlang,
    this.namaAgen,
    this.footerMessage,
    this.dataList,
  });
  EdcPrintPayloadModel.fromJson(Map<String, dynamic> json) {
    namaFitur = json['namaFitur']?.toString();
    isCetakUlang = json['isCetakUlang']?.toString();
    namaAgen = json['namaAgen']?.toString();
    footerMessage = json['footerMessage']?.toString();
    dataList = List.generate(json['dataList'].length,
        (i) => DataListModel.fromJson(json['dataList'][i]));
  }
  Map<String, dynamic> toJson() {
    final data = <String, dynamic>{};
    data['namaFitur'] = namaFitur;
    data['isCetakUlang'] = isCetakUlang;
    data['namaAgen'] = namaAgen;
    data['footerMessage'] = footerMessage;
    data['dataList'] = dataList;
    return data;
  }

  String toEncode() {
    final data = <String, dynamic>{};
    data['namaFitur'] = namaFitur;
    data['isCetakUlang'] = isCetakUlang;
    data['namaAgen'] = namaAgen;
    data['footerMessage'] = footerMessage;
    data['dataList'] = dataList;
    return jsonEncode(data);
  }
}

class DataListModel {
  String? lineString;
  String? isLarge;

  DataListModel({
    this.lineString,
    this.isLarge,
  });
  DataListModel.fromJson(Map<String, dynamic> json) {
    lineString = json['lineString']?.toString();
    isLarge = json['isLarge']?.toString();
  }
  Map<String, dynamic> toJson() {
    final data = <String, dynamic>{};
    data['lineString'] = lineString;
    data['isLarge'] = isLarge;
    return data;
  }
}
