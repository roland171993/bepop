package model;

public class MenuModel {
	
	private long mId;
	private String mImageURL;
	private String mText;
	private String mIconRes;

	public MenuModel() {
	}

	public MenuModel(long id, String imageURL, String text, String iconRes) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		mImageURL = imageURL;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public String getIconRes() {
		return mIconRes;
	}

	public void setIconRes(String iconRes) {
		mIconRes = iconRes;
	}

	@Override
	public String toString() {
		return mText;
	}
}
