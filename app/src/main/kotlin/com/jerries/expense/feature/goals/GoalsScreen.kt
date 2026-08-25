package com.jerries.expense.feature.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerries.expense.R
import com.jerries.expense.core.designsystem.component.AmountText
import com.jerries.expense.core.designsystem.component.EmptyContent
import com.jerries.expense.core.designsystem.component.LoadingContent
import com.jerries.expense.core.designsystem.component.SectionHeader
import com.jerries.expense.core.designsystem.theme.LocalSpacing
import com.jerries.expense.core.util.CurrencyFormatter
import com.jerries.expense.domain.model.SavingsGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GoalEvent.Error -> snackbarHostState.showSnackbar(event.message)
                GoalEvent.Saved -> snackbarHostState.showSnackbar("Goal saved")
                GoalEvent.Deleted -> snackbarHostState.showSnackbar("Goal deleted")
            }
        }
    }

    if (state.showForm) {
        GoalFormDialog(
            form = state.form,
            onNameChange = viewModel::onFormNameChange,
            onTargetChange = viewModel::onFormTargetChange,
            onSave = viewModel::onSaveGoal,
            onDismiss = { viewModel.onShowForm(false) },
            currencyCode = state.currencyCode,
        )
    }

    if (state.showContribution) {
        ContributionDialog(
            state = state.contribution,
            onAmountChange = viewModel::onContributionAmountChange,
            onConfirm = viewModel::onContributionDismiss,
            onConfirmAmount = viewModel::onContributionDismiss,
            onDismiss = { viewModel.onContributionDismiss() },
            currencyCode = state.currencyCode,
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.empty_goals_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onShowForm(true) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add goal")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.isEmpty -> EmptyContent(
                icon = Icons.Filled.Flag,
                title = stringResource(R.string.empty_goals_title),
                message = stringResource(R.string.empty_goals_message),
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                if (state.activeGoals.isNotEmpty()) {
                    items(state.activeGoals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            currencyCode = state.currencyCode,
                            onContribute = { viewModel.onShowContribution(goal.id, goal.name, false) },
                            onWithdraw = { viewModel.onShowContribution(goal.id, goal.name, true) },
                            onEdit = { viewModel.onEditGoal(goal) },
                            onDelete = { viewModel.onDeleteGoal(goal.id) },
                        )
                    }
                }
                if (state.completedGoals.isNotEmpty()) {
                    item { SectionHeader("Completed") }
                    items(state.completedGoals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            currencyCode = state.currencyCode,
                            onContribute = {},
                            onWithdraw = {},
                            onEdit = {},
                            onDelete = { viewModel.onDeleteGoal(goal.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    goal: SavingsGoal,
    currencyCode: String,
    onContribute: () -> Unit,
    onWithdraw: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.screenPadding),
        colors = if (goal.completed) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(spacing.cardPadding)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (goal.completed) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(spacing.extraSmall))
                    }
                    Text(goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Row {
                    if (!goal.completed) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp)) }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                }
            }
            Spacer(Modifier.height(spacing.small))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AmountText(amountMinor = goal.savedMinor, currencyCode = currencyCode, style = MaterialTheme.typography.bodyLarge)
                Text("/ ${CurrencyFormatter.formatMinorUnits(goal.targetMinor, currencyCode)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(spacing.extraSmall))
            LinearProgressIndicator(
                progress = { goal.progress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.extraSmall),
                color = if (goal.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text("${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!goal.completed) {
                Spacer(Modifier.height(spacing.small))
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    TextButton(onClick = onContribute) { Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(spacing.extraSmall)); Text(stringResource(R.string.goal_contribute)) }
                    TextButton(onClick = onWithdraw) { Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(spacing.extraSmall)); Text(stringResource(R.string.goal_withdraw)) }
                }
            }
        }
    }
}

@Composable
private fun GoalFormDialog(
    form: GoalFormState,
    onNameChange: (String) -> Unit,
    onTargetChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (form.isEditing) stringResource(R.string.edit_goal) else stringResource(R.string.add_goal)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                OutlinedTextField(value = form.name, onValueChange = onNameChange, label = { Text(stringResource(R.string.goal_name_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                OutlinedTextField(value = form.targetInput, onValueChange = onTargetChange, label = { Text(stringResource(R.string.goal_target_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
            }
        },
        confirmButton = { TextButton(onClick = onSave, enabled = form.targetInput.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

@Composable
private fun ContributionDialog(
    state: ContributionState,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onConfirmAmount: () -> Unit,
    onDismiss: () -> Unit,
    currencyCode: String,
) {
    val spacing = LocalSpacing.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (state.isWithdraw) stringResource(R.string.goal_withdraw) else stringResource(R.string.goal_contribute)) },
        text = {
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = onAmountChange,
                label = { Text(stringResource(R.string.amount_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmAmount(); onConfirm() }, enabled = state.amountInput.isNotBlank()) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
