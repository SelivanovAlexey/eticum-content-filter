package com.eticum.utils;

import com.eticum.App;
import com.eticum.R;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

import static com.eticum.filter.FilterStream.REASON_AGE;
import static com.eticum.filter.FilterStream.REASON_DISALLOW;
import static com.eticum.filter.FilterStream.REASON_NOT_ALLOW;

@UtilityClass
public class FilterUtils {
    private static final Map<Integer, String> reasonMapping = new HashMap<Integer, String>() {
        {
            put(REASON_AGE, App.getContext().getString(R.string.age_error));
            put(REASON_NOT_ALLOW, App.getContext().getString(R.string.not_in_allowed_categories_in_profile));
            put(REASON_DISALLOW, App.getContext().getString(R.string.in_denied_categories_in_profile));
        }
    };

    public static String generateRestrctedHtml(int accessCode, String uri) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang=\"en\"><head>")
                //styles
                .append("<style>\n" +
                        "      .mainBox {\n" +
                        "        width: 80%;\n" +
                        "        background-color: whitesmoke;\n" +
                        "        font-family: Arial, Helvetica, sans-serif;\n" +
                        "        margin: auto;\n" +
                        "        margin-top: 10%;\n" +
                        "        padding: 2%;\n" +
                        "        height: 2%px;\n" +
                        "        display: flex;\n" +
                        "        align-items: center;\n" +
                        "        flex-direction: column;\n" +
                        "        justify-content: center;\n" +
                        "        border-radius: 20px;\n" +
                        "      }\n" +
                        "    </style>")
                .append("<meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">")
                .append("<title>").append(App.getContext().getString(R.string.access_denied_title)).append("</title>")
                .append("</head>\n" +
                        "  <body>\n" +
                        "    <div class=\"mainBox\">   \n" +
                        "      <img src=\"data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyMi4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1\n" +
                        "      JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMyIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3\n" +
                        "      d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA2MzAuMyAxOTcuMSIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNjMwLjMgMTk3LjE7IiB4bWw6c3BhY2U9I\n" +
                        "      nByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMzOUI1NEE7fQ0KCS5zdDF7ZmlsbDojRkZGRkZGO30NCgkuc3Qye2ZpbGw6IzAxMDEwMTt9DQo8L3N0eWxlPg0KPHRpdGxlPtGN0YLQ\n" +
                        "      uNC60YPQvF/Qs9C+0YDQuNC30L7QvdGC0LDQu9GM0L3Ri9C5PC90aXRsZT4NCjxwb2x5Z29uIGNsYXNzPSJzdDAiIHBvaW50cz0iODUuMywxOTcuMSAwLDE0Ny44IDAsNDkuMyA4NS4zLDAgMTcwLjcsNDkuMyAxNzAuNyw\n" +
                        "      xNDcuOCAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iMTAuOSw1OCAxMC45LDk2LjMgNDQsNzcuMSAxMC45LDU4ICIvPg0KPHBvbHlnb24gY2xhc3M9InN0MSIgcG9pbnRzPSIxMi40LDU1LjUgNDUuNSwzNi\n" +
                        "      4zIDQ1LjUsNzQuNiAxMi40LDU1LjUgIi8+DQo8cG9seWdvbiBjbGFzcz0ic3QxIiBwb2ludHM9IjQ4LjQsMzYuMyA0OC40LDc0LjYgODEuNiw1NS41IDQ4LjQsMzYuMyAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBva\n" +
                        "      W50cz0iNDkuOCwzMy44IDgzLDE0LjcgODMsNTMgNDkuOCwzMy44ICIvPg0KPHBvbHlnb24gY2xhc3M9InN0MSIgcG9pbnRzPSI4NS45LDE0LjcgODUuOSw1Mi45IDExOS4xLDMzLjggODUuOSwxNC43ICIvPg0KPHBvbHln\n" +
                        "      b24gY2xhc3M9InN0MSIgcG9pbnRzPSI4Ny40LDU1LjUgMTIwLjUsMzYuMyAxMjAuNSw3NC42IDg3LjQsNTUuNSAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iMTIzLjQsMzYuMyAxMjMuNCw3NC42IDE1Ni4\n" +
                        "      2LDU1LjUgMTIzLjQsMzYuMyAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iMTIzLjQsNzkuNiAxNTYuNiw5OC44IDEyMy40LDExNy45IDEyMy40LDc5LjYgIi8+DQo8cG9seWdvbiBjbGFzcz0ic3QxIiBwb2\n" +
                        "      ludHM9IjEyNC45LDEyMC40IDE1OCwxMDEuMyAxNTguMSwxMzkuNiAxMjQuOSwxMjAuNCAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iMTIzLjQsMTIzIDEyMy40LDE2MS4yIDE1Ni42LDE0Mi4xIDEyMy40L\n" +
                        "      DEyMyAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iODcuNCwxNDIuMSAxMjAuNSwxMjIuOSAxMjAuNSwxNjEuMiA4Ny40LDE0Mi4xICIvPg0KPHBvbHlnb24gY2xhc3M9InN0MSIgcG9pbnRzPSI4NS45LDE0\n" +
                        "      NC42IDg1LjksMTgyLjkgMTE5LjEsMTYzLjcgODUuOSwxNDQuNiAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iNDkuOCwxNjMuNyA4MywxNDQuNiA4MywxODIuOSA0OS44LDE2My43ICIvPg0KPHBvbHlnb24\n" +
                        "      gY2xhc3M9InN0MSIgcG9pbnRzPSIxMC45LDExOC40IDI1LjcsMTA5LjggNDQuMSwxMjAuNCAxMC45LDEzOS42IDEwLjksMTE4LjQgIi8+DQo8cG9seWdvbiBjbGFzcz0ic3QxIiBwb2ludHM9IjEyLjQsMTQyLjEgNDUuNS\n" +
                        "      wxMjIuOSA0NS41LDE0NC4xIDMwLjcsMTUyLjcgMTIuNCwxNDIuMSAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iMjguNiwxMDguMiA0NS41LDk4LjQgNDUuNSwxMTcuOSAyOC42LDEwOC4yICIvPg0KPHBvb\n" +
                        "      Hlnb24gY2xhc3M9InN0MSIgcG9pbnRzPSI0OC40LDEyMi45IDQ4LjQsMTQyLjUgNjUuMywxMzIuNyA0OC40LDEyMi45ICIvPg0KPHBvbHlnb24gY2xhc3M9InN0MSIgcG9pbnRzPSI0OC40LDk2LjcgNjMuMiw4OC4yIDgx\n" +
                        "      LjYsOTguOCA0OC40LDExNy45IDQ4LjQsOTYuNyAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iNDkuOSwxMjAuNCA4MywxMDEuMyA4MywxMjIuNSA2OC4yLDEzMSA0OS45LDEyMC40ICIvPg0KPHBvbHlnb24\n" +
                        "      gY2xhc3M9InN0MSIgcG9pbnRzPSI2Ni4xLDg2LjUgODMsNzYuNyA4Myw5Ni4zIDY2LjEsODYuNSAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBvaW50cz0iODUuOSwxMjAuOCA4NS45LDEwMS4zIDEwMi44LDExMS4xID\n" +
                        "      g1LjksMTIwLjggIi8+DQo8cG9seWdvbiBjbGFzcz0ic3QxIiBwb2ludHM9Ijg1LjksNzUuMSA4NS45LDk2LjMgMTE5LjEsNzcuMSAxMDAuNyw2Ni41IDg1LjksNzUuMSAiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDEiIHBva\n" +
                        "      W50cz0iMTA1LjcsMTA5LjQgMTIwLjUsMTAwLjggMTIwLjUsNzkuNiA4Ny40LDk4LjggMTA1LjcsMTA5LjQgIi8+DQo8cG9seWdvbiBjbGFzcz0ic3QxIiBwb2ludHM9IjE1OC4xLDU4IDEyNC45LDc3LjEgMTU4LjEsOTYu\n" +
                        "      MyAxNTguMSw1OCAiLz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yNTMuMywxMjQuNHY4LjZIMjAzVjYzLjdoNDguOXY4LjZoLTM5djIxLjNoMzQuN3Y4LjRoLTM0Ljd2MjIuNEgyNTMuM3oiLz4NCgk8cGF0aCB\n" +
                        "      jbGFzcz0ic3QyIiBkPSJNMjgzLjEsNzIuNGgtMjMuOHYtOC42aDU3LjN2OC42aC0yMy43VjEzM2gtOS44VjcyLjR6Ii8+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTMyNy40LDYzLjdoOS45VjEzM2gtOS45VjYzLjd6Ii\n" +
                        "      8+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTM3MC4zLDEyOS4yYy01LjYtMy4xLTkuOS03LjMtMTMuMS0xMi43Yy0zLjItNS40LTQuOC0xMS40LTQuOC0xOC4yYzAtNi43LDEuNi0xMi44LDQuOC0xOC4yDQoJCWMzLjItN\n" +
                        "      S40LDcuNi05LjYsMTMuMi0xMi43YzUuNi0zLjEsMTEuOS00LjYsMTguOC00LjZjNS40LDAsMTAuNCwwLjksMTQuOCwyLjdjNC41LDEuOCw4LjMsNC41LDExLjUsOC4xbC02LjQsNi4yDQoJCWMtNS4yLTUuNS0xMS43LTgu\n" +
                        "      Mi0xOS41LTguMmMtNS4xLDAtOS44LDEuMi0xNCwzLjVzLTcuNCw1LjUtOS44LDkuNmMtMi4zLDQuMS0zLjUsOC42LTMuNSwxMy42czEuMiw5LjYsMy41LDEzLjYNCgkJYzIuMyw0LjEsNS42LDcuMiw5LjgsOS42YzQuMiw\n" +
                        "      yLjMsOC44LDMuNSwxNCwzLjVjNy43LDAsMTQuMi0yLjgsMTkuNS04LjNsNi40LDYuMmMtMy4yLDMuNi03LDYuMy0xMS41LDguMXMtOS41LDIuOC0xNC45LDIuOA0KCQlDMzgyLjIsMTMzLjgsMzc1LjksMTMyLjMsMzcwLj\n" +
                        "      MsMTI5LjJ6Ii8+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTQzNi41LDEyNS45Yy01LjEtNS4zLTcuNy0xMi45LTcuNy0yMi44VjYzLjdoOS45djM5YzAsMTQuOSw2LjUsMjIuMywxOS41LDIyLjNjNi4zLDAsMTEuMi0xL\n" +
                        "      jgsMTQuNi01LjUNCgkJYzMuNC0zLjcsNS05LjMsNS0xNi44di0zOWg5LjZ2MzkuNGMwLDEwLTIuNiwxNy42LTcuNywyMi44Yy01LjEsNS4yLTEyLjMsNy45LTIxLjYsNy45QzQ0OC45LDEzMy44LDQ0MS43LDEzMS4yLDQz\n" +
                        "      Ni41LDEyNS45eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik01NzEuOSwxMzNsLTAuMS01MC41bC0yNSw0Mi4xaC00LjZsLTI1LTQxLjhWMTMzaC05LjVWNjMuN2g4LjFsMjguOSw0OC43bDI4LjUtNDguN2g4LjFsMC4\n" +
                        "      xLDY5LjNINTcxLjl6Ii8+DQo8L2c+DQo8L3N2Zz4NCg==\" width=\"70%\"/>")
                .append("<h2>").append(App.getContext().getString(R.string.access_denied_msg)).append("</h2>")
                .append("<p>").append("URL: ").append(uri).append("</p>")
                .append("<p>").append(App.getContext().getString(R.string.reason)).append(": ").append(reasonMapping.get(accessCode)).append("</p>")
                .append("    </div>\n" +
                        " </body>\n" +
                        "</html>");
        return sb.toString();
    }
}
