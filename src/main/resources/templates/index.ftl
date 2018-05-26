<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>StringFormatter</title>

  <!-- Bootstrap core CSS -->
  <link rel="stylesheet" href="/webjars/bootstrap/4.0.0-2/css/bootstrap.css"/>
  <link rel="stylesheet" href="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>
  <script src="/webjars/jquery/3.2.1/jquery.min.js"></script>
  <script src="/webjars/popper.js/1.12.5/dist/umd/popper.min.js"></script>
  <script src="/webjars/bootstrap/4.0.0-2/js/bootstrap.min.js"></script>

</head>

<body class="bg-dark">

<div class="cover-container d-flex w-100 h-100 p-3 mx-auto flex-column">
  <header class="masthead mb-auto text-center">
    <div class="inner">
      <h3 class="masthead-brand" style="color:white;">String Formatter</h3>
    </div>
  </header>
  <div class="form-row" style="">
    <div class="form-group col-12">
      <label class="text-white"><a href="/example.png" target=_blank>Example</a></label>
    </div>
  </div>
  <main role="main" class="inner cover">
    <form action="/" method="POST" enctype="multipart/form-data">
      <div class="form-row mb-1">
        <div class="form-group col-12">
          <label class="text-white">Delimiter or Type</label>
          <select class="form-control" name="delimiter" id="delimiter">
            <option value="tab">Tab</option>
            <option value="comma">,</option>
            <option value="space">Space</option>
            <option value="xlsx">ExcelFile</option>
          </select>
        </div>
      </div>
      <div class="form-row mb-1" id="fileContainer">
        <div class="form-group col-12">
          <label class="text-white">File</label>
          <input class="form-control" type="file" name="file" id="file">
        </div>
      </div>
      <div class="form-row mb-1" id="dataContainer">
        <div class="form-group col-12">
          <label class="text-white">Data</label>
          <textarea class="form-control" name="data" rows="5">${data!""}</textarea>
        </div>
      </div>
      <div class="form-row mb-1">
        <div class="form-group col-12">
          <label class="text-white">Template(<a
                  href="https://freemarker.apache.org/docs/index.html">Freemarker</a>)</label>
          <textarea class="form-control" name="format">${format!""}</textarea>
        </div>
      </div>
      <div class="form-row mb-1">
        <div class="form-group col-12">
          <button class="btn btn-danger btn-block" type="submit">Convert</button>
        </div>
      </div>

      <#if error??>
        <div class="form-row">
          <div class="form-group col-12">
            <label class="text-white">Error</label>
            <pre class="form-text text-white" style="white-space:pre-wrap;">${error}</pre>

          </div>
        </div>
      <#else>
        <div class="form-row" style="">
          <div class="form-group col-12">
            <label class="text-white">Result</label>
            <textarea class="form-control mh-100" name="" rows="10">${result!""}</textarea>
          </div>
        </div>
      </#if>
    </form>

  </main>


</div>
<script>
  function statusRefresh() {
    var val = delimiterSelect.val();
    var fileContainer = $("#fileContainer");
    var dataContainer = $("#dataContainer");
    if (val === 'xlsx') {
      fileContainer.show();
      dataContainer.hide();
    } else {
      fileContainer.hide();
      dataContainer.show();
    }
  }

  var delimiterSelect = $("#delimiter");
  delimiterSelect.change(function () {
    statusRefresh();
  });

  <#if delimiter??>
    delimiterSelect.val("${delimiter}");
  </#if>

  statusRefresh();

</script>

</body>
</html>
