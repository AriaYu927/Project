# Author:LYH
import base64
import time

from django.http import HttpResponse, JsonResponse, HttpRequest
from django.shortcuts import render
from django.template.defaulttags import now
from django.template.loader import get_template
import datetime
import pymysql
import json


# 按登录按钮后服务端获取用户的用户名和密码，并将登录记录录入数据库
def login(request):
    flag = 0
    # 获取从客户端输入的用户名和密码
    date = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    name = request.POST['name']
    password = request.POST['password']

    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    # SQL 查询语句
    cursor.execute("SELECT U_NAME, U_PASSWORD FROM user")
    data = fetchJson(cursor)
    # print(data)
    for i in range(data.__len__()):
        if name == data[i]['U_NAME'] and password == data[i]['U_PASSWORD']:
            # SQL 插入语句
            sql = "INSERT INTO login(`L_DATE`, `U_NAME`) " \
                  "VALUES ('%s', '%s')" % (date, name)
            try:
                # 执行sql语句
                flag = cursor.execute(sql)
                # 执行sql语句
                db.commit()
            except Exception as ex:
                # 发生错误时回滚
                db.rollback()
                print(ex)

        # 服务端返回给客户端，登录失败或成功
    result = {
        'date': date,
        'user': name,
        'password': password,
        'state': flag,  # 0 成功，1 失败
    }
    # 关闭数据库连接
    db.close()
    return JsonResponse(flag, safe=False)


# 将注册的信息传入数据库
def register(request):
    flag = 0
    name = request.POST['name']
    password = request.POST['password']
    phone = request.POST['phone']
    userID = request.POST['idCard']
    resource = request.POST['resource']
    # 服务端返回给客户端，登录失败或成功
    result = {
        'name': name,
        'password': password,
        'phone': phone,
        'userID': userID,
        'resource': resource,
    }
    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    # SQL 插入语句
    sql = "INSERT INTO user(`U_NAME`, `U_GENDER`, `U_PASSWORD`, `U_PHONE`, `U_NUM`) " \
          "VALUES ('%s', '%s',  '%s',  '%s', '%s')" % (name, resource, password, phone, userID)
    try:
        # 执行sql语句
        flag = cursor.execute(sql)
        # 执行sql语句
        db.commit()
    except Exception as ex:
        # 发生错误时回滚
        db.rollback()
        print(ex)

    # 关闭数据库连接
    db.close()
    return JsonResponse(flag, safe=False)


def selectInsp(request):
    trace_id = request.POST['value']
    startTime = request.POST['startTime']
    endTime = request.POST['endTime']

    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()
    cursor.execute("SELECT INSP_ID FROM inspection ORDER BY INSP_ID DESC limit 1")
    data = fetchJson(cursor)

    show_bottom = {
        'data': data,
        'road_name': trace_id,
        'startTime': startTime,
        'endTime': endTime,
    }
    db.close()
    return JsonResponse(show_bottom, safe=False)


# 数据页面选择路段名称时，开始巡检，将巡检信息输入到数据库中
def insertInsp(request):
    trace_id = request.POST['value']
    startTime = request.POST['startTime']
    endTime = request.POST['endTime']
    inspId = request.POST['inspNum']

    print(trace_id)
    result = {
        'road_name': trace_id,
        'startTime': startTime,
        'endTime': endTime,
        'inspNum': inspId,
    }

    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    # SQL 插入语句

    sql = "INSERT INTO inspection(`TRACE_ID`, `INSP_DATE`, `INSP_END_DATE`) " \
          "VALUES ('%s', '%s', '%s')" % (trace_id, startTime, endTime)
    try:
        # 执行sql语句
        flag = cursor.execute(sql)
        # 执行sql语句
        db.commit()
    except Exception as ex:
        # 发生错误时回滚
        db.rollback()
        print(ex)
    db.close()
    return JsonResponse(result, safe=False)


def fly_data(request):
    thisTime = request.POST['thisTime']
    speed = request.POST['speed']
    height = request.POST['height']
    result = {
        'thisTime': thisTime,
        'speed': speed,
        'height': height,
    }
    return JsonResponse(result)


# 将输入的预警信息存入数据库
def warning(request):
    flag = 0
    warning_text = request.POST['warning']
    warn_Time = request.POST['warnTime']
    inspId = request.POST['inspId']
    print(warning_text)
    result = {
        'warnTime': warn_Time,
        'inspId': inspId,
    }
    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    cursor.execute("SELECT * FROM uva")
    uva = fetchJson(cursor)

    # SQL 插入语句
    sql = "INSERT INTO warning(`INSP_ID`,`WARN_DATE`, `W_SUMMARY`) " \
          "VALUES ('%s', '%s', '%s')" % (inspId, warn_Time, warning_text)
    try:
        # 执行sql语句
        flag = cursor.execute(sql)
        # 执行sql语句
        db.commit()
    except Exception as ex:
        # 发生错误时回滚
        db.rollback()
        print(ex)

    db.close()
    return JsonResponse(result, safe=False)


# =========================================================================
def getdb(request):
    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")
    # 使用cursor()方法获取操作游标
    cursor = db.cursor()
    result = {
        'state': 0,
        'result': ''
    }

    # 使用execute方法执行SQL语句
    cursor.execute("SELECT TRACE_ID,TRACE_ADR from trace")
    # 使用 fetchone() 方法获取一条数据
    data = fetchJson(cursor)
    # 关闭数据库连接
    db.close()
    print(data)
    return JsonResponse(data, safe=False)


# 将execute方法生成的data转换成json data
def fetchJson(cursor):
    row_headers = [x[0] for x in cursor.description]
    rv = cursor.fetchall()
    json_data = []
    for result in rv:
        json_data.append(dict(zip(row_headers, result)))
    return json_data


def getInsp(request):
    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")
    # 使用cursor()方法获取操作游标
    insp1 = db.cursor()
    insp2 = db.cursor()
    warn1 = db.cursor()
    warn2 = db.cursor()
    ser1 = db.cursor()
    ser2 = db.cursor()
    pic1 = db.cursor()
    pic2 = db.cursor()

    # 使用execute方法执行SQL语句
    insp1.execute("select i.INSP_ID, t.TRACE_ADR, i.INSP_DATE, INSP_END_DATE, u.UVA_NAME from inspection as i LEFT "
                  "JOIN uva as u ON i.TRACE_ID = u.TRACE_ID LEFT JOIN trace as t ON u.TRACE_ID = t.TRACE_ID WHERE "
                  "t.TRACE_ADR = '苑新线' ORDER BY INSP_ID;")
    insp2.execute("select i.INSP_ID, t.TRACE_ADR, i.INSP_DATE, INSP_END_DATE,u.UVA_NAME from inspection as i LEFT "
                  "JOIN uva as u ON i.TRACE_ID = u.TRACE_ID LEFT JOIN trace as t ON u.TRACE_ID = t.TRACE_ID WHERE "
                  "t.TRACE_ADR = '苑通线' ORDER BY INSP_ID;")
    warn1.execute("SELECT w.WARN_ID, w.INSP_ID, t.TRACE_ADR, w.WARN_DATE, w.W_SUMMARY, p.IMG_PATH, w.W_STATUS FROM "
                  "warning as w LEFT JOIN image as p on w.WARN_DATE = p.IMG_DATE LEFT JOIN inspection as i on "
                  "p.INSP_ID = i.INSP_ID LEFT JOIN trace as t on i.TRACE_ID = t.TRACE_ID WHERE t.TRACE_ADR = '苑新线';")
    warn2.execute("SELECT w.WARN_ID, w.INSP_ID, t.TRACE_ADR, w.WARN_DATE, w.W_SUMMARY, p.IMG_PATH, w.W_STATUS FROM "
                  "warning as w LEFT JOIN image as p on w.WARN_DATE = p.IMG_DATE LEFT JOIN inspection as i on "
                  "p.INSP_ID = i.INSP_ID LEFT JOIN trace as t on i.TRACE_ID = t.TRACE_ID WHERE t.TRACE_ADR = '苑通线';")
    ser1.execute("SELECT s.SERVI_ID, s.WARN_ID, t.TRACE_ADR, s.SERVI_DATE, s.S_STATUS, s.S_SUMMARY from service as s "
                 "LEFT JOIN warning as w on s.WARN_ID = w.WARN_ID LEFT JOIN inspection as i on w.INSP_ID = i.INSP_ID "
                 "LEFT JOIN trace as t on i.TRACE_ID = t.TRACE_ID WHERE t.TRACE_ADR = '苑新线';")
    ser2.execute("SELECT s.SERVI_ID, s.WARN_ID, t.TRACE_ADR, s.SERVI_DATE, s.S_STATUS, s.S_SUMMARY from service as s "
                 "LEFT JOIN warning as w on s.WARN_ID = w.WARN_ID LEFT JOIN inspection as i on w.INSP_ID = i.INSP_ID "
                 "LEFT JOIN trace as t on i.TRACE_ID = t.TRACE_ID WHERE t.TRACE_ADR = '苑通线';")
    pic1.execute("SELECT  p.INSP_ID, t.TRACE_ADR, p.IMG_DATE, p.IMG_PATH from image as p INNER JOIN "
                 "inspection as i ON p.INSP_ID = i.INSP_ID INNER JOIN trace as t ON i.TRACE_ID = t.TRACE_ID WHERE "
                 "i.TRACE_ID = '1';")
    pic2.execute("SELECT  p.INSP_ID, t.TRACE_ADR, p.IMG_DATE, p.IMG_PATH from image as p INNER JOIN "
                 "inspection as i ON p.INSP_ID = i.INSP_ID INNER JOIN trace as t ON i.TRACE_ID = t.TRACE_ID WHERE "
                 "i.TRACE_ID = '2';")

    # 使用 fetchone() 方法获取一条数据
    data1 = fetchJson(insp1)
    data2 = fetchJson(insp2)
    data3 = fetchJson(warn1)
    data4 = fetchJson(warn2)
    data5 = fetchJson(ser1)
    data6 = fetchJson(ser2)
    data7 = fetchJson(pic1)
    data8 = fetchJson(pic2)
    result = {
        'data1': data1,
        'data2': data2,
        'data3': data3,
        'data4': data4,
        'data7': data7,
        'data8': data8,
    }
    # 关闭数据库连接
    db.close()
    return JsonResponse(result, safe=False)


# 获取路段拍照的照片，并存入数据库
def catchImage(request):
    inspID = request.POST['inspId']
    # date1 = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    date1 = request.POST['picTime']
    result = {
        "image": "ok",
        "inspId": inspID,
        "date": date1,
    }
    # image为用户输入的‘image’
    image = request.POST['image']
    # 去掉image字符串的字头
    image = image[22:]
    # 用base64编码将字符串转换成图片
    image = base64.b64decode(image)
    # 将图片存入image文件夹
    path = "static/image/" + 'IMG_' + datetime.datetime.now().strftime('%Y%m%d%H%M%S') + ".PNG"
    date = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    file = open(path, "wb")
    file.write(image)
    file.close()

    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    # SQL 插入语句
    sql = "INSERT INTO image(`IMG_DATE`,`IMG_PATH`, `INSP_ID`) " \
          "VALUES ('%s', '%s', '%s')" % (date, path, inspID)
    try:
        # 执行sql语句
        flag = cursor.execute(sql)
        # 执行sql语句
        db.commit()
    except Exception as ex:
        # 发生错误时回滚
        db.rollback()
        print(ex)

    db.close()
    return JsonResponse(result, safe=False)


def modify(request):
    inspId = request.POST['inspId']

    # 服务端返回给客户端，登录失败或成功
    result = {
        'inspId': inspId,
    }
    # 打开数据库连接
    db = pymysql.connect(host="localhost", database="uva_database", user="root", passwd="123456")

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()

    # SQL 插入语句
    sql = "DELETE FROM inspection WHERE INSP_ID = %s" % int(inspId)
    try:
        # 执行sql语句
        flag = cursor.execute(sql)
        # 执行sql语句
        db.commit()
    except Exception as ex:
        # 发生错误时回滚
        db.rollback()
        print(ex)

    # 关闭数据库连接
    db.close()
    return JsonResponse(flag, safe=False)