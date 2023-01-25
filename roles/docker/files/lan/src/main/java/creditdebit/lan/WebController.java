package creditdebit.lan;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.lang.Math;
import java.util.regex.*;


@Controller
public class WebController {
    int min = 1;
    int max = 100000000;
    int b;
    String str1;

    DirContext connection;
    public void newconnection() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, System.getenv("HOST"));
        env.put(Context.SECURITY_PRINCIPAL, System.getenv("BIND_DN"));
        env.put(Context.SECURITY_CREDENTIALS, System.getenv("PASSWORD"));
        try {
            connection = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/listUser")
    public String listUser(@CookieValue(name = "login", defaultValue = "False") String login, Model model) throws NamingException {
        if (!login.equals(this.str1)){
            return "error";
        } else {
            this.newconnection();
            String searchFilter = "(objectClass=user)";
            String[] reqAtt = {"cn", "distinguishedName"};
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningAttributes(reqAtt);

            NamingEnumeration users = connection.search("ou=Chercheurs,"+System.getenv("BASE"), searchFilter, controls);
            NamingEnumeration users_tech = connection.search("ou=Technicien,"+System.getenv("BASE"), searchFilter, controls);
            NamingEnumeration users_glob = connection.search("cn=Users,"+System.getenv("BASE"), searchFilter, controls);

            List myList = new ArrayList();
            SearchResult result = null;
            while (users.hasMore()) {
                result = (SearchResult) users.next();
                Attributes attr = result.getAttributes();
                String name = attr.get("cn").get(0).toString();
                String dn = attr.get("distinguishedName").get(0).toString();
                Pattern pattern = Pattern.compile("OU=([^,]*)");
                Matcher matcher = pattern.matcher(dn);
                
                if (matcher.find()){
                    name = name + ": " + matcher.group(1);
                }

                myList.add(name);
            }
            
            while (users_tech.hasMore()) {
                result = (SearchResult) users_tech.next();
                Attributes attr = result.getAttributes();
                String name = attr.get("cn").get(0).toString();
                String dn = attr.get("distinguishedName").get(0).toString();
                Pattern pattern = Pattern.compile("OU=([^,]*)");
                Matcher matcher = pattern.matcher(dn);
                
                if (matcher.find()){
                    name = name + ": " + matcher.group(1);
                }

                myList.add(name);
            }

            while (users_glob.hasMore()) {
                result = (SearchResult) users_glob.next();
                Attributes attr = result.getAttributes();
                String name = attr.get("cn").get(0).toString();

                if (!name.equals("Administrator") && !name.equals("Guest") && !name.equals("krbtgt")){
                    myList.add(name);
                }
            }
            
            model.addAttribute("listUsername", myList);
            return "listUser";
        }
    }

    @GetMapping("/update")
    public String update(@CookieValue(name = "login", defaultValue = "False") String login){
        if (!login.equals(this.str1)){
            return "error";
        } else {
            return "update";
        }
    }

    @GetMapping("/delete")
    public String delete(@CookieValue(name = "login", defaultValue = "False") String login){
        if (!login.equals(this.str1)){
            return "error";
        } else {
            return "delete";
        }

    }
    @PostMapping(path="/updateUser", consumes="application/x-www-form-urlencoded")
    public String updateUser(User user, Model model) {
        model.addAttribute("username", user.getUserName());

        this.newconnection();
        try {
            String dnBase;
            if (user.getOrganization().equals("SansOU")){
                dnBase=",cn=Users,"+System.getenv("BASE");
            } else {
                dnBase=",ou="+user.getOrganization()+","+System.getenv("BASE");
            }
            ModificationItem[] mods= new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", encodePassword(user.getPassword())));
            connection.modifyAttributes("cn="+user.getUserName()+dnBase, mods);
            return "updateUser";
        }catch (Exception e) {
            System.out.println("failed: "+e.getMessage());
            return "error3";
        }

    }

    @GetMapping("/add")
    public String add(@CookieValue(name = "login", defaultValue = "False") String login){
        if (!login.equals(this.str1)){
            return "error";
        } else {
            return "add";
        }
    }

    @GetMapping("/intranet")
    public String intranet(@CookieValue(name = "login", defaultValue = "False") String login){
        if (!login.equals(this.str1)){
            return "error";
        } else {
            return "intranet";
        }
    }

    @PostMapping(path="/addUser", consumes="application/x-www-form-urlencoded")
    public String addUser(User user, Model model) {
        model.addAttribute("username", user.getUserName());
        try {
            this.newconnection();

            Attributes attributes = new BasicAttributes();
            Attribute attribute = new BasicAttribute("objectClass");
            attribute.add("organizationalPerson");
            attribute.add("top");
            attribute.add("person");
            attribute.add("user");

            Attribute pwd = new BasicAttribute("unicodePwd");
            pwd.add(this.encodePassword(user.getPassword()));
            attributes.put(pwd);

            Attribute dn = new BasicAttribute("distinguishedName");
            if (user.getOrganization().equals("SansOU")){
                dn.add("cn=" + user.getUserName() + ",cn=Users,"+System.getenv("BASE"));
            } else {
                dn.add("cn=" + user.getUserName() + ",ou=" + user.getOrganization() + ","+System.getenv("BASE"));
            }
            attributes.put(dn);
            Attribute sama = new BasicAttribute("sAMAccountName");
            sama.add(user.getUserName());
            attributes.put(sama);

            Attribute upn = new BasicAttribute("userPrincipalName");
            upn.add(user.getUserName() + "@"+System.getenv("DOMAIN"));
            attributes.put(upn);

            Attribute uac = new BasicAttribute("userAccountControl");
            uac.add("512");
            attributes.put(uac);

            Attribute cn = new BasicAttribute("cn");
            cn.add(user.getUserName());
            attributes.put(cn);

            attributes.put(attribute);
            if (user.getOrganization().equals("SansOU")){
                connection.createSubcontext("cn=" + user.getUserName() + ",cn=Users,"+System.getenv("BASE"), attributes);
            } else {
                connection.createSubcontext("cn=" + user.getUserName() + ",ou=" + user.getOrganization() + ","+System.getenv("BASE"), attributes);
            }

            return "addUser";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error2";
        }
    }

    private byte[] encodePassword(String password) {
        String quotedPassword = "\"" + password + "\"";
        return quotedPassword.getBytes(StandardCharsets.UTF_16LE);
    }

    @PostMapping(path="/deleteUser", consumes="application/x-www-form-urlencoded")
    public String deleteUser(User user, Model model ){
        model.addAttribute("username", user.getUserName());
        try {
            this.newconnection();
            if (user.getOrganization().equals("SansOU")){
                connection.destroySubcontext("cn="+user.getUserName()+",cn=Users,"+System.getenv("BASE"));
            } else {
                connection.destroySubcontext("cn="+user.getUserName()+",ou="+user.getOrganization()+","+System.getenv("BASE"));
            }
            return "deleteUser";
        } catch (NamingException e) {
            e.printStackTrace();
            return "error1";
        }
    }

    public class User {

        private String username;
        private String password;
        private String ou;

        public User(String username, String password, String ou){
            this.username = username;
            this.password = password;
            this.ou = ou;
        }

        public String getUserName(){
            return this.username;
        }

        public String getPassword(){
            return this.password;
        }

        public String getOrganization(){
            return this.ou;
        }
    }

    @PostMapping(path="/login", consumes="application/x-www-form-urlencoded")
    public String login(User user, HttpServletResponse response){
        this.newconnection();
        this.b = (int)(Math.random()*(this.max-this.min+1)+this.min);
        int seconds = (int) (System.currentTimeMillis() / 1000l);
        this.str1 = Integer.toString(b)+Integer.toString(seconds);

        try {
            Cookie cookie = new Cookie("login", str1);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, System.getenv("HOST"));
            env.put(Context.SECURITY_PRINCIPAL, "cn="+user.getUserName()+",ou=Technicien,"+System.getenv("BASE"));
            env.put(Context.SECURITY_CREDENTIALS, user.getPassword());
            DirContext con = new InitialDirContext(env);
            con.close();
            return"intranet";

        }catch (Exception e) {
            return "error";
        }
    }
}