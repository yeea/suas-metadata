package model.site.neon.jsonPOJOs;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * POJO class used in JSON deserialization
 */
public class RawNEONProductAvailability
{
	/*
	All fields below are listed because they are required for JSON serialization. None are actually used for now.
	Getters are found below too, but unused.
	 */

	private final StringProperty dataProductCode = new SimpleStringProperty(null);
	private final StringProperty dataProductTitle = new SimpleStringProperty(null);
	private final ObjectProperty<String[]> availableMonths = new SimpleObjectProperty<>(null);
	private final ObjectProperty<String[]> availableDataUrls = new SimpleObjectProperty<>(null);

	public String getDataProductCode()
	{
		return dataProductCode.get();
	}

	public StringProperty dataProductCodeProperty()
	{
		return dataProductCode;
	}

	public String getDataProductTitle()
	{
		return dataProductTitle.get();
	}

	public StringProperty dataProductTitleProperty()
	{
		return dataProductTitle;
	}

	public String[] getAvailableMonths()
	{
		return availableMonths.get();
	}

	public ObjectProperty<String[]> availableMonthsProperty()
	{
		return availableMonths;
	}

	public String[] getAvailableDataUrls()
	{
		return availableDataUrls.get();
	}

	public ObjectProperty<String[]> availableDataUrlsProperty()
	{
		return availableDataUrls;
	}
}
