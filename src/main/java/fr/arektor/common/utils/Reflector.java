package fr.arektor.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reflector {

	private static Map<Class<?>,Map<String,Field>> fields_master = new HashMap<Class<?>,Map<String,Field>>();
	private static Map<Class<?>,Map<String,Method>> methods_master = new HashMap<Class<?>,Map<String,Method>>();

	Class<?> clazz;
	Object entity;

	public Reflector(Class<?> clazz, Object entity) {
		this.clazz = clazz;
		this.entity = entity;
	}

	public Object get(String field) {
		try {
			if (getFields().containsKey(field)) return getFields().get(field).get(entity);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				return fField.get(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object invoke(String method, Object... args) {
		return invoke(method, Object.class, args);
	}

	@SuppressWarnings("unchecked")
	public <T> T invoke(String method, Class<? extends T> expectedReturnType, Object... args) {
		Object returned = null;
		try {
			if (getMethods().containsKey(method)) returned = getMethods().get(method).invoke(entity, args);
			else {
				Method m;
				if (args != null && args.length > 0) {
					List<Class<?>> paramTypes = new ArrayList<Class<?>>();
					for (Object o : args) {
						paramTypes.add(o.getClass());
					}
					m = clazz.getDeclaredMethod(method, paramTypes.toArray(new Class<?>[paramTypes.size()]));
					accessMethod(m);
					getMethods().put(method, m);
					returned = m.invoke(entity, args);
				} else {
					m = clazz.getDeclaredMethod(method);
					accessMethod(m);
					getMethods().put(method, m);
					returned = m.invoke(entity);
				}
				//methods_master.put(clazz, methods);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (returned == null) {
			if (expectedReturnType == Void.class || expectedReturnType == Object.class) return null;
			else throw new IllegalArgumentException("Expected return of type "+expectedReturnType.getSimpleName()+" from method "+method+": got none instead");
		} else {
			if (expectedReturnType.isInstance(returned)) return (T)returned;
			else if (expectedReturnType == Void.class) throw new IllegalArgumentException("Expected no return from method "+method+": got "+returned.getClass().getSimpleName()+" instead");
			else throw new IllegalArgumentException("Expected return of type "+expectedReturnType.getSimpleName()+" from method "+method+": got "+returned.getClass().getSimpleName()+" instead");
		}
	}

	public int getInt(String field) {
		try {
			if (getFields().containsKey(field)) return getFields().get(field).getInt(entity);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				return fField.getInt(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean getBoolean(String field) {
		try {
			if (getFields().containsKey(field)) return getFields().get(field).getBoolean(entity);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				return fField.getBoolean(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public float getFloat(String field) {
		try {
			if (getFields().containsKey(field)) return getFields().get(field).getFloat(entity);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				return fField.getFloat(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getDouble(String field) {
		try {
			if (getFields().containsKey(field)) return getFields().get(field).getDouble(entity);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				return fField.getDouble(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void set(String field, Object value) {
		try {
			if (getFields().containsKey(field)) getFields().get(field).set(entity, value);
			else {
				Field fField = clazz.getDeclaredField(field);
				accessField(fField);
				getFields().put(field, fField);
				//fields_master.put(clazz, fields);
				fField.set(entity, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void accessField(Field f) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
	}

	private void accessMethod(Method m) {
		m.setAccessible(true);
	}

	private Map<String,Field> getFields() {
		return Reflector.getFields(this.clazz);
	}

	private Map<String,Method> getMethods() {
		return Reflector.getMethods(this.clazz);
	}

	private static Map<String,Field> getFields(Class<?> clazz) {
		if (fields_master.containsKey(clazz)) return fields_master.get(clazz);
		else {
			Map<String,Field> map = new HashMap<String,Field>();
			fields_master.put(clazz,map);
			return map;
		}
	}

	private static Map<String,Method> getMethods(Class<?> clazz) {
		if (methods_master.containsKey(clazz)) return methods_master.get(clazz);
		else {
			Map<String,Method> map = new HashMap<String,Method>();
			methods_master.put(clazz,map);
			return map;
		}
	}

	public void setReference(Object ref) {
		this.entity = ref;
	}

	/*
	Object get(String field);
	int getInt(String field);
	double getDouble(String field);
	float getFloat(String field);
	boolean getBoolean(String field);

	void set(String field, Object value);
	 */
}
