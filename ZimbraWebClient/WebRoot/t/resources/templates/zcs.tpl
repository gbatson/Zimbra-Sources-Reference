/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
# Very simple templates file. Each template has an ID. In JS, it is available as
# ZCS.template.[id] after this file has been loaded and processed.
#
# In this file:
#   - any line that starts with a # is a comment
#   - beginning and trailing space are trimmed during processing
#
# In general, a template that begins with <tpl> is an XTemplate, and one that does
# not is an itemTpl.

<template id='ConvListItem'>
	<div class='zcs-mail-list'>
		<tpl if='isInvite'>
		<div class='zcs-mail-invitation'>
			<img src='/t/resources/images/invitation<tpl if='isUnread'>_unread</tpl>.png' />
		</div>
		<tpl else>
		<div class='zcs-mail-readState'>
			<img src='/t/resources/images/<tpl if='isUnread'>un</tpl>read.png' />
		</div>
		</tpl>
		<div class='zcs-mail-senders<tpl if='isUnread'>-unread</tpl>'>
			{senders}
		</div>
		<div class='zcs-mail-date'>{dateStr}</div>
		<tpl if='hasAttachment'>
			<div class='zcs-mail-attachment'>
			<img src='/t/resources/images/attachment.png' />
		</div>
		</tpl>
		<div class='zcs-mail-subject<tpl if='isUnread'>-unread</tpl>'>{[Ext.String.htmlEncode(values.subject)]}</div>
		<tpl if='numMsgs &gt; 1'>
			<span class='zcs-numMsgs'>{numMsgs}</span>
		</tpl>
		<tpl if='isFlagged'>
			<div class='zcs-mail-flag'>
			<img src='/t/resources/images/flagged.png' />
		</div>
		</tpl>
		<div class='zcs-mail-fragment'>{[Ext.String.htmlEncode(values.fragment)]}</div>
	</div>
</template>

# In the message header templates below, the values FROM, TO, CC, and BCC are taken
# from ZCS.constants. It is unlikely that they will change, but that's where they're defined.

<template id='CollapsedMsgHeader'>
	<tpl>
	<div class='zcs-mail-msgHdr collapsed'>
		<div class='zcs-msgHdr-person' style='{imageStyle}'></div>
		<tpl for='addrs.FROM'>
		<div class='zcs-msgHdr-fromBubble'>
			<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
		</div>
		</tpl>
		<div class='zcs-msgHdr-date'>{dateStr}</div>
		<div class='zcs-msgHdr-fragment'>{[Ext.String.htmlEncode(values.fragment)]}</div>
	</div>
	</tpl>
</template>

<template id='ExpandedMsgHeader'>
	<tpl>
	<div class='zcs-mail-msgHdr expanded'>
		<div class='zcs-msgHdr-person' style='{imageStyle}'></div>
		<tpl for='addrs.FROM'>
		<div class='zcs-msgHdr-fromBubble'>
			<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
		</div>
		</tpl>
		<div class='zcs-msgHdr-date'>{dateStr}</div>
		<tpl if='recipients'>
		<div class='zcs-msgHdr-to'>
			<span>{[ZtMsg.to]}</span>
			<span>{recipients}</span>
		</div>
		</tpl>
		<a class='zcs-msgHdr-link' onClick='return false;'>{[ZtMsg.showDetails]}</a>
		<a class='x-button-normal x-button x-iconalign-center x-layout-box-item x-stretched zcs-flat zcs-msgHdr-menuButton' onClick='return false;'>
			<span class='zcs-msgHeader-menuButton-span x-button-icon x-shown arrow_down'></span>
		</a>
		</div>
		<tpl if='tags && tags.length'>
		<div class='zcs-mail-dtlMsgHdr'>
			<div class='zcs-msgHdr-field'>
				<tpl for='tags'>
				<span class='zcs-area-bubble zcs-tag-bubble' id='{id}'>
					<div class="zcs-tag-small zcs-tag-{color}" tagName="{name}" <tpl if='rgb'>style='background-color: {rgb};'</tpl>></div>
					{displayName}
				</span>
				</tpl>
			</div>
		</div>
		</tpl>
	</div>
	</tpl>
</template>

# TODO: Put OBO display into zcs-msgHdr-from element

<template id='DetailedMsgHeader'>
	<tpl>
	<div class='zcs-mail-msgHdr detailed'>
		<div class='zcs-msgHdr-person' style='{imageStyle}'></div>
		<tpl for='addrs.FROM'>
			<div class='zcs-msgHdr-fromBubble'>
				<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
			</div>
		</tpl>
		<tpl for='addrs.FROM'>
			<div class='zcs-msgHdr-from'>{[ZtMsg.from]} {address}</div>
		</tpl>
		<div class='zcs-msgHdr-date'>{fullDateStr}</div>
		<a class='zcs-msgHdr-link' onClick='return false;'>{[ZtMsg.hideDetails]}</a>
		<a class='x-button-normal x-button x-iconalign-center zcs-flat x-layout-box-item x-stretched zcs-msgHdr-menuButton' onClick='return false;'>
			<span class='x-button-icon x-shown arrow_down'></span>
		</a>
	</div>
	<tpl if='addrs.TO'>
		<div class='zcs-mail-dtlMsgHdr'>
			<div class='zcs-msgHdr-label'>{[ZtMsg.toHdr]}</div>
			<div class='zcs-msgHdr-field'>
				<tpl for='addrs.TO'>
				<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
				</tpl>
			</div>
		</div>
	</tpl>
	<tpl if='addrs.CC'>
		<div class='zcs-mail-dtlMsgHdr'>
			<div class='zcs-msgHdr-label'>{[ZtMsg.ccHdr]}</div>
			<div class='zcs-msgHdr-field'>
				<tpl for='addrs.CC'>
				<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
				</tpl>
			</div>
		</div>
	</tpl>
	<tpl if='tags && tags.length'>
		<div class='zcs-mail-dtlMsgHdr'>
			<div class='zcs-msgHdr-label'>{[ZtMsg.tagsHdr]}</div>
			<div class='zcs-msgHdr-field'>
				<tpl for='tags'>
				<span class='zcs-area-bubble zcs-tag-bubble' id='{id}'>
					<div class="zcs-tag-small zcs-tag-{color}" tagName="{name}" <tpl if='rgb'>style='background-color: {rgb};'</tpl>></div>
					{displayName}
				</span>
			</tpl>
		</div>
		</div>
	</tpl>
	</tpl>
</template>

<template id='ContactListItem'>
	<div class='zcs-contactListItem'>
		<tpl if='isGroup'>
			<div class='zcs-contactList-group' <tpl if='imageUrl'>style='background-image:url({imageUrl})'</tpl>></div>
		<tpl else>
			<div class='zcs-contactList-person' <tpl if='imageUrl'>style='background-image:url({imageUrl})'</tpl>></div>
		</tpl>
		<div class='zcs-contactList-text'>
			<tpl if='isGroup'>
				<div class='zcs-contactList-name'>{nickname}</div>
			<tpl else>
				<div class='zcs-contactList-name'>{nameLastFirst}</div>
				<div class='zcs-contactList-title'>{job}</div>
			</tpl>
		</div>
	</div>
</template>

<template id='Contact'>
	<tpl if='isGroup'>
		<div class='zcs-contactgroupview-header'>
			<div class='zcs-contactgroupview-image'></div>
			<div class='zcs-contactgroupview-personalInfo'>
				<span name="contactname">{nickname}</span>
			</div>
		</div>
		<div class='zcs-contactgroupview-members'>
			<tpl for='groupMembers'>
				<div class='zcs-contactgroupview-member'>
					<div class='zcs-contact-image' <tpl if='memberImageUrl'>style='background-image:url({memberImageUrl})'</tpl>></div>
					<div class='zcs-contact-info'>
						<span name="contactname">{longName}</span>
						<tpl if='jobTitle'><span>{jobTitle}</span></tpl>
						<span>{memberEmail}</span>
						<span>{memberPhone}</span>
					</div>
				</div>
			</tpl>
		</div>
	<tpl else>
		<div class='zcs-contactview-header'>
			<div class='zcs-contactview-image' style='{imageStyle}'></div>
			<div class='zcs-contactview-personalInfo'>
				<span name="contactname">{fullName}</span>
				<tpl if='jobTitle'><span>{jobTitle}</span></tpl>
				<tpl if='company'><span>{company}</span></tpl>
			</div>
		</div>
		<div class='zcs-contactview-fieldSets'>
			<tpl if='email'>
				<div class='zcs-contactview-fieldSet'>
					<div class='zcs-contactview-label'>{[ZtMsg.email]}</div>
					<div class='zcs-contactview-fields'>
						<tpl for='email'>
							<div class='zcs-contactview-field'>{email}</div>
							<div class='zcs-contactview-subLabel'>{typeStr}</div>
						</tpl>
					</div>
				</div>
			</tpl>
			<tpl if='phone'>
				<div class='zcs-contactview-fieldSet'>
					<div class='zcs-contactview-label'>{[ZtMsg.phone]}</div>
					<div class='zcs-contactview-fields'>
						<tpl for='phone'>
							<div class='zcs-contactview-field'><a href="tel:{phone}">{phone}</a></div>
							<div class='zcs-contactview-subLabel'>{typeStr}</div>
						</tpl>
					</div>
				</div>
			</tpl>
			<tpl if='address.length'>
				<div class='zcs-contactview-fieldSet'>
					<div class='zcs-contactview-label'>{[ZtMsg.address]}</div>
					<div class='zcs-contactview-fields'>
						<tpl for='address'>
							<div class='zcs-contactview-field'>
								<tpl if='mapAddr'><a href='http://maps.apple.com/?q={mapAddr}'></tpl>
									<span class='zcs-contactview-street'>{street}</span>
									<tpl if='city'>
										<span class='zcs-contactview-city'>{city},&nbsp</span>
									</tpl>
									<span class='zcs-contactview-state'>{state}&nbsp</span>
									<span class='zcs-contactview-postalcode'>{postalCode}</span>
									<span class='zcs-contactview-country'>{country}</span>
								<tpl if='mapAddr'></a></tpl>
							</div>
							<div class='zcs-contactview-subLabel'>{typeStr}</div>
						</tpl>
					</div>
				</div>
			</tpl>
			<tpl if='url'>
				<div class='zcs-contactview-fieldSet'>
					<div class='zcs-contactview-label'>{[ZtMsg.url]}</div>
					<div class='zcs-contactview-fields'>
						<tpl for='url'>
							<div class='zcs-contactview-field'>{url}</div>
							<div class='zcs-contactview-subLabel'>{typeStr}</div>
						</tpl>
					</div>
				</div>
			</tpl>
		</div>
		</tpl>
</template>

#widgets/_assignmentview.scss
<template id='TagAssignmentListItem'>
	<div class="zcs-tag zcs-tag-{color} zcs-tag-large" <tpl if='rgb'>style="background-color: {rgb};"</tpl>></div>{displayName}
</template>

<template id="InviteDesc">
	<tpl>
		<table class='zcs-invite'>
			<tpl if='start==end'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invDateLabel]}</td>
				<td>{start}</td>
			</tr>
			<tpl else>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invStartLabel]}</td>
				<td>{start}&nbsp;<span style='font-size: 0.75em;'>({timezone})</span></td>
			</tr>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invEndLabel]}</td>
				<td>{end}&nbsp;</td>
			</tr>
			</tpl>
			<tpl if='location'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invLocationLabel]}</td>
				<td>{location}</td>
			</tr>
			</tpl>
			<tpl if='organizer'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invOrganizerLabel]}</td>
				<td>
					<span class='zcs-area-bubble zcs-contact-bubble' id='{organizer.id}'>{organizer.name}</span>
				</td>
			</tr>
			</tpl>
			<tpl if='sentBy'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invSentByLabel]}</td>
				<td>
					<span class='zcs-area-bubble zcs-contact-bubble' id='{sentBy.id}'>{sentBy.name}</span>
				</td>
			</tr>
			</tpl>
			<tpl if='attendees'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invAttendeesLabel]}</td>
				<td>
				<tpl for='attendees'>
					<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{name}</span>
				</tpl>
				</td>
			</tr>

			</tpl>
			<tpl if='attendeeResponse && attendeeResponseMsg'>
				<tr>
					<tpl if="attendeeResponse == [ZCS.constant.PSTATUS_ACCEPTED]">
						<td colspan="2" class="zcs-invite-status-accept">
							<span> {attendeeResponseMsg} </span>
						</td>
					</tpl>
					<tpl if="attendeeResponse == [ZCS.constant.PSTATUS_DECLINED]">
						<td colspan="2" class="zcs-invite-status-decline">
							<span> {attendeeResponseMsg} </span>
						</td>
					</tpl>
					<tpl if="attendeeResponse == [ZCS.constant.PSTATUS_TENTATIVE]">
						<td colspan="2" class="zcs-invite-status-tentative">
							<span> {attendeeResponseMsg} </span>
						</td>
					</tpl>
				</tr>
			</tpl>
			<tpl if='optAttendees'>
				<tr>
					<td class='zcs-invite-label'>{[ZtMsg.invOptionalAttendeesLabel]}</td>
					<td>
						<tpl for='attendees'>
							<span class='zcs-area-bubble zcs-contact-bubble' id='{id}'>{displayName}</span>
						</tpl>
					</td>
				</tr>
			</tpl>
			<tpl if='myResponse'>
				<tr>
					<td class='zcs-invite-label'>{[ZtMsg.invStatusLabel]}</td>
					<td>{myResponse}</td>
				</tr>
			</tpl>

			<tpl if='showButtons'>
				<tr class='zcs-invite-buttons'>
					<td class='zcs-invite-label'>{[ZtMsg.invRespondLabel]}</td>
					<td>
						<tpl if='myResponse == [ZCS.constant.PSTATUS_TEXT[ZCS.constant.PSTATUS_ACCEPTED]]'>
							<span class='zcs-invite-button zcs-accept-inactive'>{[ZtMsg.acceptAction]}</span>
						<tpl else>
							<span class='zcs-invite-button zcs-invite-accept' id='{acceptButtonId}'>{[ZtMsg.acceptAction]}</span>
						</tpl>

						<tpl if='myResponse == [ZCS.constant.PSTATUS_TEXT[ZCS.constant.PSTATUS_TENTATIVE]]'>
							<span class='zcs-invite-button zcs-tentative-inactive'>{[ZtMsg.tentativeAction]}</span>
						<tpl else>
							<span class='zcs-invite-button zcs-invite-tentative' id='{tentativeButtonId}'>{[ZtMsg.tentativeAction]}</span>
						</tpl>

						<tpl if='myResponse == [ZCS.constant.PSTATUS_TEXT[ZCS.constant.PSTATUS_DECLINED]]'>
							<span class='zcs-invite-button zcs-decline-inactive'>{[ZtMsg.declineAction]}</span>
						<tpl else>
							<span class='zcs-invite-button zcs-invite-decline' id='{declineButtonId}'>{[ZtMsg.declineAction]}</span>
						</tpl>
					</td>
				</tr>
			</tpl>

			<tpl if='intendedFor'>
				<tr>
					<td class='zcs-invite-label'>{[ZtMsg.invIntendedFor]}</td>
					<td>
						<span class='zcs-area-bubble zcs-contact-bubble'>{intendedFor}</span>
					</td>
				</tr>
			</tpl>
		</table>

	</tpl>
</template>

<template id='InviteNotes'>
	<tpl>
		<hr>
		<div class='zcs-invite-notes'>{notes}</div>
	</tpl>
</template>

<template id='ConvListSwipeToDelete'>
	<tpl>
		<div class='zcs-swipe-conv-view' style='width:{width}px;height:{height}px;'>
			<div class='zcs-swipe-delete x-button x-button-delete'>{[ZtMsg.del]}</div>
		</div>
	</tpl>
</template>

<template id='Toast'>
	<div class="zcs-toast-contents">
		<div class="zcs-toast-status-icon"></div>
		<div class="zcs-toast-message-text">{text}</div>
		<div class="zcs-toast-undo-action">{[ZtMsg.undo]}</div>
	</div>
</template>

# show a single attachment
<template id='Attachment'>
	<span class='zcs-area-bubble zcs-attachment-bubble' id="{id}"><div class='{icon}'></div>{label} <span>({size})</span></span>
</template>

<template id='QuotedLink'>
	<div class='zcs-quoted-link'>
	<tpl if='show'>
		{[ZtMsg.showQuotedText]}
	<tpl else>
		{[ZtMsg.hideQuotedText]}
	</tpl>
	</div>
</template>

<template id='Truncated'>
	<div class='zcs-truncated-message'><span>{[ZtMsg.messageTruncated]}</span> <span class='zcs-truncated-message-link'>{[ZtMsg.messageTruncatedLink]}</span></div>
</template>

<template id='OriginalAttachments'>
	<div class='zcs-link'>{[ZtMsg.addOriginalAttachments]}</div>
</template>

<template id='Folder'>
	<tpl if='unreadCount'><b></tpl>{name}<tpl if='unreadCount'> ({unreadCount})</b></tpl>
</template>

<template id="ApptViewAttendeeList">
	<span style="float: left;width: 60%">{attendee.name}</span>

	<tpl if='isOrganizer'>
		<tpl if='attendee.ptst == [ZCS.constant.PSTATUS_ACCEPTED]'>
			<span class="view-appt-accepted">{[ZtMsg.accepted]}</span>
		</tpl>

		<tpl if='attendee.ptst == [ZCS.constant.PSTATUS_DECLINED]'>
			<span class="view-appt-declined">{[ZtMsg.declined]}</span>
		</tpl>

		<tpl if='attendee.ptst == [ZCS.constant.PSTATUS_TENTATIVE]'>
			<span class="view-appt-tentative"> {[ZtMsg.tentative]} </span>
		</tpl>

		<tpl if='attendee.ptst == [ZCS.constant.PSTATUS_UNKNOWN]'>
			<span class="view-appt-unknown"> {[ZtMsg.unknown]} </span>
		</tpl>
	</tpl>
</template>



<template id="ApptViewDesc">
	<tpl>
		<tpl if='start'>
			<div class="view-appt-header">{start}</div>
		</tpl>


		<table class='zcs-invite' style="padding: 15px; text-align: left">

			<tpl if='location'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invLocationLabel]}</td>
				<td>{location}</td>
			</tr>
			</tpl>

			<tpl if='organizer'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invOrganizerLabel]}</td>
				<td>
					<span>{organizer}</span>
				</td>
			</tr>
			</tpl>


			<tpl if='attendees'>
			<tr>
				<td class='zcs-invite-label'>{[ZtMsg.invAttendeesLabel]}</td>
				<td>
					<span>{attendees} <span id="showAttendees" class='view-appt-showDetails'> {[ZtMsg.showDetails]} </span></span>
				</td>
			</tr>
			</tpl>


			<tpl if='calendar'>
				<tr>
					<td class='zcs-invite-label'>Calendar</td>
					<td>
					   <span>{calendar}</span>
					</td>
				</tr>
			</tpl>


			<tpl if='reminder'>
				<tr>
					<td class='zcs-invite-label'>{[ZtMsg.invReminderLabel]}</td>
					<td>{reminder}</td>
				</tr>
			</tpl>

		</table>

		<tpl if='notes'>
			<div class="view-appt-notes">
				<hr>
				<br>
				{notes}

			</div>
		</tpl>
	</tpl>
</template>
