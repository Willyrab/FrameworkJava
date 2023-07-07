<%-- 
    Document   : form
    Created on : 7 juil. 2023, 01:57:37
    Author     : Daniella
--%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="../assets/css/bootstrap.min.css">
        <title>Document</title>
    </head>
    <body>
        <div class="container">
            <form action="emp-save" method="post" class="well">
                <div class="form-group">
                    <p><label for="nom">Nom : </label></p>
                    <p><input type="text" name="nom" id="nom" class="form-control"></p>
                </div>
                <div class="form-group">
                    <p><label for="dateNaissance">Date de naissance : </label></p>
                    <p><input type="date" name="dateNaissance" id="dateNaissance" class="form-control"></p>
                </div>
                <div class="form-group">
                    <p><label for="salaire">Salaire : </label></p>
                    <p><input type="text" name="salaire" id="salaire" class="form-control"></p>
                </div>
                <div class="form-group">
                    <input type="submit" value="Creer" class="btn btn-success">
                </div>
            </form>
        </div>
    </body>
</html>