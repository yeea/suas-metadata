package model.elasticsearch.query.conditions;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.elasticsearch.query.ElasticSearchQuery;
import model.elasticsearch.query.QueryCondition;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * Data model used by the "Start date filter" query condition
 */
public class StartDateCondition extends QueryCondition
{
	// The current start date
	private ObjectProperty<LocalDateTime> startDate = new SimpleObjectProperty<>(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0));

	/**
	 * This query condition ensures only images with date > startDate are selected
	 *
	 * @param query The current state of the query before the appending
	 */
	@Override
	public void appendConditionToQuery(ElasticSearchQuery query)
	{
		query.setStartDate(startDate.getValue());
	}

	/**
	 * Returns the FXML document that can edit this data model
	 *
	 * @return An FXML UI document to edit this data model
	 */
	@Override
	public String getFXMLConditionEditor()
	{
		return "StartDateCondition.fxml";
	}

	/**
	 * Returns the start date property used for UI binding
	 *
	 * @return The start date property
	 */
	public ObjectProperty<LocalDateTime> startDateProperty()
	{
		return this.startDate;
	}
}
