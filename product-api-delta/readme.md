# Product Api changes

* Make changes to the api `@PatchMapping(value = "/{productId}")` to return only the delta. Containing:
   * Changes coming from the payload
   * Any other changes coming from imcscript, triggers or groovy scripts
   
* Add a RequestParam isSimulation to take a boolean value
   * `products/:id?isSimulation=true`
   * Should be defaulted to false
   * false = existing behaviour, should commit to DB
   * true = should not commit and return the delta response.

## Approach
* Using the Processing graph from the context, we can get List of changes in `Triple` format i.e. :
  ```java
    final FlushableDelta processDelta = com.imc.context.Utils.getProcessGraph(ctx);
    final List<Triple> additions = GraphUtil.findAll(processDelta.getAdditions()).toList();
    final List<Triple> deletions = GraphUtil.findAll(processDelta.getDeletions()).toList();
  ```
* And we can use these List to form our result
* [Code changes](https://github.com/in-mind-cloud/development/blob/7a785d9a34c4a893ca25c344a1bca8f46137c341/product/iss/core/src/main/java/com/imc/iss/web/services/util/CreateBOFromTriples.java#L338)


## Problems
1. **How to handle the deletions**
   * Currently, in Patch operation we are not performing any deletions
2. **How to handle updating data using multiple endpoints**
   * for example, you update something in the account using `accounts/{id}` api, then update something in the address (using address end point - `accounts/{id}/addresses`), 
   * should we require the previous account changes too to maintain the state?
3. **How to handle the cyclic dependents**
    * like includesSalesItem and salesItemIncludedBy or containsQuote vs quoteContainedBy
    * We are forming response from a set of Triples, so, we will not face this problem as we are not retrieving any data.
4. **Creating dependent resources using Patch operation**
    * As of now, if we don't provide id for a child resource(like Address inside Account) it will create it and a random id is generated for it.
    * But when `isSimulation=true` we are not doing a commit to DB, thus that Address doesn't exist in the system.
    * So, if we then pass that delta payload with `isSimulation=false`, it will reject it saying that Address doesn't exist.
    * **Possible Solution**: remove the id of the created resource from payload.
    * How to identify if the child resource is created or updated?
       * Currently, identifying it if the Addition Triples contains: `Schema.objectId` or `Schema.objectDateOfCreation`
