package jboost.util;

import java.util.Vector;

public class ObjectPool {
	
	private ParameterObject paraObj;//�ö���ص����Բ�������
	private Class<?> clsType;//�ö����������Ŷ��������
	private int currentNum = 0; //�ö���ص�ǰ�Ѵ����Ķ�����Ŀ
	private Object currentObj;//�ö���ص�ǰ���Խ���Ķ���
	private Vector<Object> pool;//���ڴ�Ŷ���ĳ�
	
	public ObjectPool(ParameterObject paraObj, Class<?> clsType) {
		this.paraObj = paraObj;
		this.clsType = clsType;
		pool = new Vector<Object>();
	}
	
	public Object getObject() {
		if (pool.size() <= paraObj.getMinCount()) {
			if (currentNum <= paraObj.getMinCount()) {
				//�����ǰ�����޶�����ã������Ѵ����Ķ�����ĿС�������Ƶ����ֵ��������
				//PoolObjectFactory����һ���µĶ���
				PoolableObjectFactory objFactory =PoolableObjectFactory.getInstance();
				currentObj = objFactory.createObject(clsType);
				currentNum++;
			} else {
				//�����ǰ�����޶�����ã������������Ķ�����Ŀ�Ѵﵽ�����Ƶ����ֵ��
				//��ֻ�ܵȴ������̷߳��ض��󵽳���
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					currentObj = pool.firstElement();	
				}
			}
		} else {
			//�����ǰ�����п��õĶ��󣬾�ֱ�Ӵӳ���ȡ������
			currentObj = pool.firstElement();
		}
		
		return currentObj;
	}
	
	public void returnObject(Object obj) {
		// ȷ�����������ȷ������
		if (((Class<?>) obj).isInstance(clsType)) {
			pool.addElement(obj);
			synchronized (this) {
				notifyAll();
			}
		} else {
			throw new IllegalArgumentException("�ö���ز��ܴ��ָ���Ķ�������");
		}
	}
	
	public Object createObject(Class<?> clsType) {
		Object obj = null;
		try {
			obj = clsType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public ObjectPool createPool(ParameterObject paraObj,Class<?> clsType) {
		return new ObjectPool(paraObj, clsType);
	}
}
