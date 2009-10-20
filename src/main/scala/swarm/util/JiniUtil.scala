package swarm

import net.jini.core.discovery.LookupLocator
import net.jini.space.{JavaSpace05}
import java.io.{FileDescriptor, IOException}
import java.rmi.RMISecurityManager
import java.security.Permission
import java.net.InetAddress
import net.jini.discovery.LookupLocatorDiscovery
import net.jini.lease.LeaseRenewalManager
import net.jini.core.lookup.{ServiceItem, ServiceTemplate, ServiceRegistrar}
import net.jini.lookup.{ServiceDiscoveryEvent, ServiceDiscoveryListener, LookupCache, ServiceDiscoveryManager}
import java.lang.{String, Class}
import net.jini.id.Uuid
import net.jini.core.lease.Lease

object JiniUtil {
  val lusHost: String = "jini://cgbspender/"
  val serviceTemplate: ServiceTemplate = new ServiceTemplate(null, Array(classOf[JavaSpace05]), null)
  var sdm: ServiceDiscoveryManager = null
  var lookupCache: LookupCache = null

  def registerNode(serverName : String) : NodeIdentifier = {
    val identifier: NodeIdentifier = NodeIdentifier.newIdentifier(serverName)
    getSpaceSDM().write(identifier, null, Lease.FOREVER);
    identifier
  }


  def getSpace(): JavaSpace05 = {
    try {
      val reg: ServiceRegistrar = new LookupLocator(lusHost).getRegistrar()
      val space = reg.lookup(new ServiceTemplate(null, Array(classOf[JavaSpace05]), null)).asInstanceOf[JavaSpace05]
      if (space == null) throw new RuntimeException("not found")
      space
    } catch {
      case e: IOException => throw new RuntimeException(e);
      case e: ClassNotFoundException => throw new RuntimeException(e);
    }
  }

  def getSpaceSDM(): JavaSpace05 = {
    var item: ServiceItem = null
    var listener: ServiceDiscoveryListener = new LocalListener
    if (sdm == null) {
      println("creating SDM and lookup cache")
      try {
        val locator: LookupLocator = new LookupLocator("jini://cgbspender/")
        sdm = new ServiceDiscoveryManager(new LookupLocatorDiscovery(Array(locator)), new LeaseRenewalManager())

        // non-blocking lookup; we may get lucky on the first try
        item = sdm.lookup(serviceTemplate, null, 0)

        // create a lookup cache for later searches
        lookupCache = sdm.createLookupCache(serviceTemplate, null, listener)
        println(format("lookup cache: %s", lookupCache))
      } catch {
        case e: IOException => throw new RuntimeException(e);
        case e: ClassNotFoundException => throw new RuntimeException(e);
      }
    }
    var cnt = 0
    while (item == null && cnt < 5) {
      cnt = cnt + 1
      item = lookupCache.lookup(null)
      if (item == null || item.service == null) {
        println("not found, sleeping")
        Thread.sleep(1000)
      }
    }
    if (item == null || item.service == null) {
      throw new RuntimeException("can't locate an instance of JavaSpace on the network")
    } else {
      println(format("found item after %d tries: %s", cnt, item))
    }
    val space = item.service.asInstanceOf[JavaSpace05]
    if (space == null) throw new RuntimeException("not found")
    space
  }
}

class LocalListener extends ServiceDiscoveryListener {
  def serviceAdded(event: ServiceDiscoveryEvent) {println(format("added: %s", event))}

  def serviceRemoved(event: ServiceDiscoveryEvent) {println(format("removed: %s", event))}

  def serviceChanged(event: ServiceDiscoveryEvent) {println(format("changed: %s", event))}
}

class NoOpSecurityManager extends RMISecurityManager {
  override def checkPermission(perm: Permission) {
    //
  }

  override def checkPermission(perm: Permission, context: AnyRef) {
    //
  }

  override def checkCreateClassLoader() {
    //
  }

  override def checkAccess(t: Thread) {
    //
  }

  override def checkAccess(g: ThreadGroup) {
    //
  }

  override def checkExit(status: Int) {
    //
  }

  override def checkExec(cmd: String) {
    //
  }

  override def checkLink(lib: String) {
    //
  }

  override def checkRead(fd: FileDescriptor) {
    //
  }

  override def checkRead(file: String) {
    //
  }

  override def checkRead(file: String, context: Object) {
    //
  }

  override def checkWrite(fd: FileDescriptor) {
    //
  }

  override def checkWrite(file: String) {
    //
  }

  override def checkDelete(file: String) {
    //
  }

  override def checkMemberAccess(clazz: Class[_], which: Int) = {}

  override def checkConnect(host: String, port: Int) {
    //
  }

  override def checkConnect(host: String, port: Int, context: AnyRef) {
    //
  }

  override def checkListen(port: Int) {
    //
  }

  override def checkAccept(host: String, port: Int) {
    //
  }

  override def checkMulticast(maddr: InetAddress) {
    //
  }

  override def checkMulticast(maddr: InetAddress, ttl: Byte) {
    //
  }

  override def checkPropertiesAccess() {
    //
  }

  override def checkPropertyAccess(key: String) {
    //
  }

  override def checkTopLevelWindow(window: AnyRef): Boolean = true

  override def checkPrintJobAccess() {
    //
  }

  override def checkSystemClipboardAccess() {
    //
  }

  override def checkAwtEventQueueAccess() {
    //
  }

  override def checkPackageAccess(pkg: String) {
    //
  }

  override def checkPackageDefinition(pkg: String) {
    //
  }

  override def checkSetFactory() {
    //
  }

  override def checkSecurityAccess(target: String): Unit = {
    //
  }
}
