package starcorp.common.types;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

public abstract class AFactoryItem extends AItemType {

	public boolean isComponent(AItemType type) {
		Set<Items> items = getComponent();
		for(Items item : items) {
			if(item.getTypeClass().equals(type))
				return true;
		}
		return false;
	}
	
	public Set<Items> getComponent() {
		Set<Items> items = new TreeSet<Items>();
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.indexOf(getKey() + ".component.type") != -1) {
				String componentNumber = key.substring(key.lastIndexOf(".") + 1);
				Items item = new Items();
				item.setType(bundle.getString(key));
				try {
					item.setQuantity(Integer.parseInt(bundle.getString(getKey() + ".component.qty." + componentNumber)));
				}
				catch(MissingResourceException e) {
					// ignore
				}
				catch(NumberFormatException e) {
					// ignore
				}
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public int getNPCPrice() {
		int price = 0;
		for(Items item : getComponent()) {
			price += (item.getQuantity() * item.getTypeClass().getNPCPrice());
		}
		price = (int) Math.round((double) price * 1.20);
		return price;
	}
	
}