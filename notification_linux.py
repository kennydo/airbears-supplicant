import dbus
import gobject

from dbus.mainloop.glib import DBusGMainLoop
from log import log

class NetworkStatus(object):
    NM_SERVICE = 'org.freedesktop.NetworkManager'
    NM_OBJECT = '/org/freedesktop/NetworkManager'
    NM_INTERFACE = 'org.freedesktop.NetworkManager.Device.Wireless'

    NM_DEVICE_STATE_ACTIVATED = 100

    def handler(self, properties):
        if 'State' in properties:
            if properties['State'] == self.NM_DEVICE_STATE_ACTIVATED:
                self.connection_callback()

    def __init__(self, connection_callback):
        self.connection_callback = connection_callback

    def register(self):
        DBusGMainLoop(set_as_default=True)
        bus = dbus.SystemBus()
        try:
            bus.add_signal_receiver(self.handler,
                                    dbus_interface=self.NM_INTERFACE,
                                    signal_name='PropertiesChanged')
        except dbus.DBusException, e:
            log(e)

        loop = gobject.MainLoop()
        loop.run()

if __name__ == "__main__":
    def test_callback():
        from pprint import pprint
        pprint("Something changed!")

    ns = NetworkStatus(test_callback)
    ns.register()
