package model;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used to index metadata into the elasticsearch index
 */
public class MetadataIndexer
{
	// The IP of the elastic search index
	private static final String ELASTIC_SEARCH_HOST = "128.196.38.73";
	// The port of the elastic search index
	private static final Integer ELASTIC_SEARCH_PORT = 9200;
	// The scheme used to connect to the elastic search index
	private static final String ELASTIC_SEARCH_SCHEME = "http";

	// The index to index images into
	private static final String ELASTIC_SEARCH_INDEX = "drone";
	// The type that each image is indexed as
	private static final String ELASTIC_SEARCH_TYPE = "_doc";

	// The converter used to convert raw metadata into index metadata
	private MetadataConverter metadataConverter = new MetadataConverter();

	/**
	 * Indexes given raw metadata into the elastic search cluster
	 *
	 * @param rawMetadata The mapping of exif tag -> exif value to index
	 */
	public void indexSingle(Map<String, String> rawMetadata)
	{
		// First convert our raw metadata into something indexable
		Map<String, Object> cleanedMetadata = this.metadataConverter.convertRawToIndexable(rawMetadata);

		if (cleanedMetadata == null || cleanedMetadata.isEmpty())
		{
			DroneLogger.logDebug("Not enough metadata was present on the image to index it, ignore the file.");
			System.exit(0);
		}

		// Open a DB connection
		try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(ELASTIC_SEARCH_HOST, ELASTIC_SEARCH_PORT, ELASTIC_SEARCH_SCHEME))))
		{
			// Create an index request
			IndexRequest request = new IndexRequest()
					.index(ELASTIC_SEARCH_INDEX)
					.type(ELASTIC_SEARCH_TYPE)
					.source(cleanedMetadata);

			// Execute the indexing process and get the response
			IndexResponse response = client.index(request);

			// Print out the response code
			DroneLogger.logDebug("Index response: " + response.status());
		}
		// This means our connection to the elasticsearch index wasn't successful, so throw an error
		catch (IOException e)
		{
			DroneLogger.logError("Error connecting to the elasticsearch client!");
			e.printStackTrace();
		}
	}

	/**
	 * Indexes a list of exif tag -> exif value mappings all at once with bulk optimization
	 *
	 * @param rawMetadataList The list of raw metadata to index
	 */
	public void indexBulk(List<Map<String, String>> rawMetadataList)
	{
		// Map the raw metadata to the conversion function which will give us a list of cleaned up metadata
		List<Map<String, Object>> cleanedMetadataList = rawMetadataList
				.stream()
				// Convert each metadata object to its indexable counterpart
				.map(this.metadataConverter::convertRawToIndexable)
				// Remove any null mappings which mean the metadata was invalid
				.filter(Objects::nonNull)
				// Remove any mappings that are empty (meaning no metadata was found)
				.filter(stringObjectMap -> stringObjectMap.size() > 0)
				.collect(Collectors.toList());

		// Make sure we have at least one image that is ready to be indexed
		if (cleanedMetadataList.isEmpty())
		{
			DroneLogger.logDebug("No images contained sufficient metadata to be indexed, they were all ignored.");
			System.exit(0);
		}

		// Open a DB connection
		try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(ELASTIC_SEARCH_HOST, ELASTIC_SEARCH_PORT, ELASTIC_SEARCH_SCHEME))))
		{
			// Create a bulk index request
			BulkRequest bulkRequest = new BulkRequest();

			// Create an index request for each metadata mapping
			cleanedMetadataList.forEach(cleanedMetadata ->
			{
				IndexRequest request = new IndexRequest()
						.index(ELASTIC_SEARCH_INDEX)
						.type(ELASTIC_SEARCH_TYPE)
						.source(cleanedMetadata);
				bulkRequest.add(request);
			});

			// Execute the insert and save the response
			BulkResponse response = client.bulk(bulkRequest);

			// Print the response and any failures if there were any
			DroneLogger.logDebug("Index response: " + response.status());
			if (response.hasFailures())
				DroneLogger.logDebug(response.buildFailureMessage());
		}
		// If the connection to the elasticsearch server fails catch it here
		catch (IOException e)
		{
			DroneLogger.logDebug("Error connecting to the elasticsearch client!");
			e.printStackTrace();
		}
	}
}
