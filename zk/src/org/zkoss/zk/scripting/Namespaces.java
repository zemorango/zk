/* Namespaces.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 16 00:01:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * {@link Namespace} relevant utilities.
 *
 * @author tomyeh
 */
public class Namespaces {
	private static final Log log = Log.lookup(Namespaces.class);

	/** Prepares builtin variable before calling {@link Page#interpret}.
	 *
	 * <p>Typical use:
	 * <pre><code>
final Map backup = new HashMap();
final Namespace ns = Namespaces.beforeInterpret(backup, comp);
try {
  Namespaces.backupVariable(backup, ns, "some");
  page.interpret(zslang, zscript, ns);
} finally {
  Namespaces.afterInterpret(backup, ns);
}
</code></pre>
	 * </code></pre>
	 *
	 * <p>If you need to backup some variables, you can invoke
	 * {@link #backupVariable} between {@link #beforeInterpret}
	 * and {@link #afterInterpret}.
	 *
	 * @param backup the map to hold the backup variables. Never null.
	 * @param comp the component, never null.
	 * @return the namespace that owns the specified component
	 */
	public static final Namespace beforeInterpret(Map backup, Component comp) {
		final Namespace ns = comp.getNamespace();

		backupVariable(backup, ns, "self");
		backupVariable(backup, ns, "componentScope");
		backupVariable(backup, ns, "arg");

		ns.setVariable("self", comp, true);
		ns.setVariable("componentScope",
			comp.getAttributes(Component.COMPONENT_SCOPE), true);

		final Execution exec = Executions.getCurrent();
		final Object arg = exec != null ? exec.getArg(): null;
		ns.setVariable("arg", arg, true);

		return ns;
	}
	/** Prepares builtin variable before calling
	 * {@link org.zkoss.zk.ui.Page#interpret}.
	 *
	 * @see #beforeInterpret
	 * @param backup the map to hold the backup variables. Never null.
	 * @param page the page, never null.
	 * @return the namespace that owns the specified page
	 */
	public static final Namespace beforeInterpret(Map backup, Page page) {
		final Namespace ns = page.getNamespace();

		backupVariable(backup, ns, "arg");

		final Execution exec = Executions.getCurrent();
		final Object arg = exec != null ? exec.getArg(): null;
		ns.setVariable("arg", arg, true);

		return ns;
	}
	/** Used with {@link #beforeInterpret} to clean up builtin
	 * variables.
	 *
	 * @param backup the map to hold the backup variables. Never null.
	 * It must be the same as the backup argument of {@link #beforeInterpret}.
	 * @param ns the namespace returned by {@link #beforeInterpret}
	 */
	public static final void afterInterpret(Map backup, Namespace ns) {
		for (Iterator it = backup.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String name = (String)me.getKey();
			final Object val = me.getValue();
			//if (D.ON && log.finerable()) log.finer("Restore "+name+"="+val);
			if (val != VOID) ns.setVariable(name, val, true);
			else ns.unsetVariable(name, true);
		}
	}

	/** Backup the specfied variable, such that it can be restored with
	 * {@link #afterInterpret}.
	 *
	 * <p>Note: you have to invoke {@link #beforeInterpret} before calling
	 * this method. Then, backup-ed variables will be restored together
	 * when {@link #afterInterpret} is called.
	 *
	 * @param backup the map to hold the backup variables. Never null.
	 * It must be the same as the backup argument of {@link #beforeInterpret}.
	 * @param name the variable to backup.
	 */
	public static final void backupVariable(Map backup, Namespace ns, String name) {
		final Object val = ns.getVariable(name, true);
		backup.put(name,
			val != null || ns.getVariableNames().contains(name) ? val: VOID);
	}
	private static final Object VOID = new Object();

	/** Returns the current namespace.
	 * The current namespace is the event target's namespace if this thread
	 * is processing an event ({@link org.zkoss.zk.ui.event.Event#getTarget}.
	 * Otherwise, the namespace of the page specified is assumed.
	 *
	 * <p>This method is used only to implement {@link org.zkoss.zk.scripting.Interpreter}.
	 * You rarely need to access it other than implementing an interpreter.
	 */
	public static final Namespace getCurrent(Page page) {
		final List nss = (List)_curnss.get();
		final Namespace ns =
			nss != null && !nss.isEmpty() ? (Namespace)nss.get(0): null;
		return ns != null ? ns: page.getNamespace();
	}
	/** Pushes the specified namespace as the current namespace.
	 *
	 * <p>It must be called as follows.
	 * <pre><code>
Namespaces.pushCurrent(ns);
try {
	...
} finally {
	Namespaces.pop();
}
	 </code></pre>
	 *
	 * <p>In most case, it is used with {@link #beforeInterpret} as follows.
	 * <pre><code>
final Map backup = new HashMap();
final Namespace ns = Namespaces.beforeInterpret(backup, comp);
Namespaces.pushCurrent(ns);
try {
  Namespaces.backupVariable(backup, ns, "some");
  page.interpret(zslang, zscript, ns);
} finally {
  Namespaces.afterInterpret(backup, ns);
  Namespaces.popCurrent();
}
</code></pre>
	 *
	 * <p>Application developer rarely needs to call this method, unless
	 * he wants to simulate the context of a particular namespace
	 * for interpreter to evaluate (mostly methods of classes defined
	 * in scripting codes).
	 *
	 * @param ns the namespace. If null, it means page's namespace.
	 */
	public static final void pushCurrent(Namespace ns) {
		List nss = (List)_curnss.get();
		if (nss == null)
			_curnss.set(nss = new LinkedList());
		nss.add(0, ns);
	}
	/** Pops the current namespce (pushed by {@link #pushCurrent}).
	 */
	public static final void popCurrent() {
		final List nss = (List)_curnss.get();
		if (nss == null || nss.isEmpty())
			log.error("Illegal popCurrent: empty stack");
		nss.remove(0);
	}

	/** A stack of current namespace. */
	private static final ThreadLocal _curnss = new ThreadLocal();
}
