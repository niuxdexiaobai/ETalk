package com.zhy.dialtong.fragment.contacts;

import java.util.Comparator;

import com.zhy.dialtong.ContactBean;



public class PinyinComparator implements Comparator<ContactBean>{
	
	public int compare(SortModel o1, SortModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

	@Override
	public int compare(ContactBean lhs, ContactBean rhs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
