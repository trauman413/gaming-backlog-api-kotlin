package com.gamingbacklog.api.gamingbacklogapi.unit.testutil.constants

object ResponseConstants {
  const val mockIGDBAuthValidResponse = "{\"access_token\":\"test_secret\",\"expires_in\":5180593,\"token_type\":\"bearer\"}"
  const val mockIGDBAuth400Response = "{\"status\":400,\"message\":\"invalid\"}"
  const val mockIGDBGetGame200Response = "[\n" +
    "  {\n" +
    "    \"id\": 191411,\n" +
    "    \"artworks\": [\n" +
    "      {\n" +
    "        \"id\": 74625,\n" +
    "        \"url\": \"//images.igdb.com/igdb/image/upload/t_thumb/ar1lkx.jpg\"\n" +
    "      }\n" +
    "    ],\n" +
    "    \"franchises\": [\n" +
    "      {\n" +
    "        \"id\": 1932,\n" +
    "        \"name\": \"Xenoblade\"\n" +
    "      }\n" +
    "    ],\n" +
    "    \"genres\": [\n" +
    "      {\n" +
    "        \"id\": 12,\n" +
    "        \"name\": \"Role-playing (RPG)\"\n" +
    "      },\n" +
    "      {\n" +
    "        \"id\": 31,\n" +
    "        \"name\": \"Adventure\"\n" +
    "      }\n" +
    "    ],\n" +
    "    \"involved_companies\": [\n" +
    "      {\n" +
    "        \"id\": 163667,\n" +
    "        \"company\": {\n" +
    "          \"id\": 1119,\n" +
    "          \"name\": \"Monolith Soft\"\n" +
    "        }\n" +
    "      },\n" +
    "      {\n" +
    "        \"id\": 163668,\n" +
    "        \"company\": {\n" +
    "          \"id\": 70,\n" +
    "          \"name\": \"Nintendo\"\n" +
    "        }\n" +
    "      }\n" +
    "    ],\n" +
    "    \"name\": \"Xenoblade Chronicles 3\",\n" +
    "    \"platforms\": [\n" +
    "      {\n" +
    "        \"id\": 130,\n" +
    "        \"name\": \"Nintendo Switch\"\n" +
    "      }\n" +
    "    ],\n" +
    "    \"release_dates\": [\n" +
    "      {\n" +
    "        \"id\": 354757,\n" +
    "        \"human\": \"Jul 29, 2022\"\n" +
    "      }\n" +
    "    ],\n" +
    "    \"summary\": \"Eunie's the bussss\"\n" +
    "  }\n" +
    "]"
  const val mockIGDBGetGame400Response = "Status Code: 400, Error: [\n" +
    "  {\n" +
    "    \"title\": \"Syntax Error\",\n" +
    "    \"status\": 400\n" +
    "  }\n" +
    "]"
}