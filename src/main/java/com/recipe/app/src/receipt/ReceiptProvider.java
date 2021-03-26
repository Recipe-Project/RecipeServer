package com.recipe.app.src.receipt;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.secret.Secret;
import com.recipe.app.src.user.models.*;
import com.recipe.app.src.userRecipe.UserRecipeRepository;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class ReceiptProvider {
}
