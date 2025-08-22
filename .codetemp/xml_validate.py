from lxml import etree
p='C:/Users/sajed/AndroidStudioProjects/PlasTech/app/src/main/res/layout/fragment_monitor.xml'
try:
    etree.parse(p)
    print('OK')
except etree.XMLSyntaxError as e:
    print('XMLSyntaxError:', e)
except Exception as e:
    print('ERROR', e)
